package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.entity.Message;
import com.quickerrand.mapper.MessageMapper;
import com.quickerrand.service.MessageService;
import com.quickerrand.vo.MessageVO;
import com.quickerrand.websocket.MessagePushHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息通知服务实现
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired(required = false)
    private MessagePushHandler messagePushHandler;

    @Override
    public List<MessageVO> getUserMessages(Long userId) {
        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .orderByDesc(Message::getCreateTime)
        );

        return messages.stream().map(message -> {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(message, vo);

            switch (message.getType()) {
                case 1:
                    vo.setTypeText("订单消息");
                    break;
                case 2:
                    vo.setTypeText("系统消息");
                    break;
                case 3:
                    vo.setTypeText("聊天消息");
                    break;
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getIsRead, 0)
        ).intValue();
    }

    @Override
    public void markAsRead(Long userId, Long messageId) {
        messageMapper.update(null,
                new LambdaUpdateWrapper<Message>()
                        .eq(Message::getId, messageId)
                        .eq(Message::getUserId, userId)
                        .set(Message::getIsRead, 1)
        );
        log.info("用户{}标记消息{}为已读", userId, messageId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        messageMapper.update(null,
                new LambdaUpdateWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getIsRead, 0)
                        .set(Message::getIsRead, 1)
        );
        log.info("用户{}标记所有消息为已读", userId);
    }

    @Override
    public void pushMessage(Long userId, String title, String content, Integer type, Long relatedId) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setRelatedId(relatedId);
        message.setIsRead(0);
        messageMapper.insert(message);

        log.info("推送消息给用户{}，标题：{}，类型：{}", userId, title, type);

        MessageVO messageVO = new MessageVO();
        BeanUtils.copyProperties(message, messageVO);
        messageVO.setTypeText(getTypeText(type));

        if (messagePushHandler != null) {
            messagePushHandler.pushToUser(userId, messageVO);
        }
    }

    @Override
    public void pushMessageToUsers(List<Long> userIds, String title, String content, Integer type, Long relatedId) {
        for (Long userId : userIds) {
            pushMessage(userId, title, content, type, relatedId);
        }
    }

    @Override
    public void deleteMessage(Long userId, Long messageId) {
        messageMapper.delete(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getId, messageId)
                        .eq(Message::getUserId, userId)
        );
        log.info("用户{}删除消息{}", userId, messageId);
    }

    @Override
    public Page<MessageVO> getUserMessagesPage(Long userId, Integer pageNum, Integer pageSize, Integer type) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId);
        
        if (type != null) {
            wrapper.eq(Message::getType, type);
        }
        
        wrapper.orderByDesc(Message::getCreateTime);

        Page<Message> page = new Page<>(pageNum, pageSize);
        messageMapper.selectPage(page, wrapper);

        Page<MessageVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<MessageVO> voList = page.getRecords().stream().map(message -> {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(message, vo);
            vo.setTypeText(getTypeText(message.getType()));
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Long sendSystemMessage(String title, String content, Long relatedId) {
        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setType(2);
        message.setRelatedId(relatedId);
        message.setIsRead(0);
        messageMapper.insert(message);

        log.info("创建系统消息，标题：{}，ID：{}", title, message.getId());

        MessageVO messageVO = new MessageVO();
        BeanUtils.copyProperties(message, messageVO);
        messageVO.setTypeText("系统消息");

        if (messagePushHandler != null) {
            messagePushHandler.pushToAll(messageVO);
        }

        return message.getId();
    }

    @Override
    public Long sendSystemMessageToUser(Long userId, String title, String content, Long relatedId) {
        pushMessage(userId, title, content, 2, relatedId);
        return null;
    }

    private String getTypeText(Integer type) {
        if (type == null) {
            return "未知类型";
        }
        switch (type) {
            case 1:
                return "订单消息";
            case 2:
                return "系统消息";
            case 3:
                return "聊天消息";
            default:
                return "未知类型";
        }
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        messageMapper.delete(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getRelatedId, orderId)
                        .eq(Message::getType, 1)
        );
        log.info("删除订单相关消息，订单ID：{}", orderId);
    }

    @Override
    public void deleteByOrderIds(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return;
        }
        messageMapper.delete(
                new LambdaQueryWrapper<Message>()
                        .in(Message::getRelatedId, orderIds)
                        .eq(Message::getType, 1)
        );
        log.info("批量删除订单相关消息，订单ID列表：{}", orderIds);
    }
}
