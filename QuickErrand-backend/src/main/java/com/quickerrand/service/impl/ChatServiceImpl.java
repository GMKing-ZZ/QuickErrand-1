package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.quickerrand.entity.ChatMessage;
import com.quickerrand.entity.ChatOrderRel;
import com.quickerrand.entity.Order;
import com.quickerrand.entity.User;
import com.quickerrand.exception.BusinessException;
import com.quickerrand.mapper.ChatMessageMapper;
import com.quickerrand.mapper.ChatOrderRelMapper;
import com.quickerrand.mapper.OrderMapper;
import com.quickerrand.mapper.UserMapper;
import com.quickerrand.service.ChatService;
import com.quickerrand.service.MessageService;
import com.quickerrand.vo.ChatContactVO;
import com.quickerrand.vo.ChatConversationVO;
import com.quickerrand.vo.ChatMessageVO;
import com.quickerrand.vo.ChatOrderConversationVO;
import com.quickerrand.vo.ChatPeerInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天会话与消息服务实现
 *
 * @author 周政
 * @date 2026-02-10
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private static final int DEFAULT_PAGE_SIZE = 50;

    @Autowired
    private ChatOrderRelMapper chatOrderRelMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageService messageService;

    @Override
    public ChatOrderRel ensureChatSession(Long currentUserId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(currentUserId) && (order.getRunnerId() == null || !order.getRunnerId().equals(currentUserId))) {
            throw new BusinessException("无权访问该订单聊天");
        }
        if (order.getRunnerId() == null) {
            throw new BusinessException("订单尚未分配跑腿员，暂不支持在线聊天");
        }

        // 查询是否已有会话绑定
        ChatOrderRel rel = chatOrderRelMapper.selectOne(
                new LambdaQueryWrapper<ChatOrderRel>()
                        .eq(ChatOrderRel::getOrderId, orderId)
        );

        if (rel != null) {
            // 如果被软删除，则恢复
            if (rel.getDeleteTime() != null) {
                rel.setDeleteTime(null);
                chatOrderRelMapper.updateById(rel);
            }
            return rel;
        }

        // 创建新的会话绑定
        ChatOrderRel newRel = new ChatOrderRel();
        newRel.setOrderId(orderId);
        newRel.setUserId(order.getUserId());
        newRel.setRunnerId(order.getRunnerId());
        chatOrderRelMapper.insert(newRel);

        log.info("创建订单聊天会话绑定，orderId={}, userId={}, runnerId={}", orderId, order.getUserId(), order.getRunnerId());
        return newRel;
    }

    @Override
    public List<ChatConversationVO> getUserConversations(Long currentUserId) {
        // 查询当前用户相关的会话绑定
        List<ChatOrderRel> relList = chatOrderRelMapper.selectList(
                new LambdaQueryWrapper<ChatOrderRel>()
                        .and(wrapper -> wrapper.eq(ChatOrderRel::getUserId, currentUserId)
                                .or()
                                .eq(ChatOrderRel::getRunnerId, currentUserId))
                        .isNull(ChatOrderRel::getDeleteTime)
        );
        if (relList.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询订单与用户信息
        List<Long> orderIds = relList.stream().map(ChatOrderRel::getOrderId).collect(Collectors.toList());
        List<Order> orders = orderMapper.selectBatchIds(orderIds);
        Map<Long, Order> orderMap = new HashMap<>();
        for (Order order : orders) {
            orderMap.put(order.getId(), order);
        }

        // 收集所有对端用户ID，批量查询用户
        List<Long> peerIds = new ArrayList<>();
        for (ChatOrderRel rel : relList) {
            Long peerId = rel.getUserId().equals(currentUserId) ? rel.getRunnerId() : rel.getUserId();
            if (peerId != null) {
                peerIds.add(peerId);
            }
        }
        List<User> users = peerIds.isEmpty() ? new ArrayList<>() : userMapper.selectBatchIds(peerIds);
        Map<Long, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }

        // 为每个会话查询最新消息和未读数
        List<ChatConversationVO> result = new ArrayList<>();
        for (ChatOrderRel rel : relList) {
            Order order = orderMap.get(rel.getOrderId());
            if (order == null) {
                continue;
            }
            Long peerId = rel.getUserId().equals(currentUserId) ? rel.getRunnerId() : rel.getUserId();
            User peer = peerId == null ? null : userMap.get(peerId);

            ChatConversationVO vo = new ChatConversationVO();
            vo.setOrderId(rel.getOrderId());
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderStatus(order.getStatus());
            vo.setPeerUserId(peerId);
            if (peer != null) {
                vo.setPeerNickname(peer.getNickname() != null ? peer.getNickname() : peer.getUsername());
                vo.setPeerAvatar(peer.getAvatar());
            } else {
                vo.setPeerNickname("未知用户");
                vo.setPeerAvatar(null);
            }

            // 最近一条消息
            ChatMessage lastMessage = chatMessageMapper.selectOne(
                    new LambdaQueryWrapper<ChatMessage>()
                            .eq(ChatMessage::getOrderId, rel.getOrderId())
                            .orderByDesc(ChatMessage::getSendTime)
                            .last("limit 1")
            );
            if (lastMessage != null) {
                vo.setLastContent(lastMessage.getContent());
                vo.setLastTime(lastMessage.getSendTime());
            }

            // 未读消息数量（当前用户为接收方且未读）
            Integer unread = chatMessageMapper.selectCount(
                    new LambdaQueryWrapper<ChatMessage>()
                            .eq(ChatMessage::getOrderId, rel.getOrderId())
                            .eq(ChatMessage::getToUserId, currentUserId)
                            .eq(ChatMessage::getReadStatus, 0)
            ).intValue();
            vo.setUnreadCount(unread);

            result.add(vo);
        }

        // 按最近消息时间倒序排序
        result.sort((a, b) -> {
            Long t1 = a.getLastTime() != null ? a.getLastTime() : 0L;
            Long t2 = b.getLastTime() != null ? b.getLastTime() : 0L;
            return Long.compare(t2, t1);
        });

        return result;
    }

    @Override
    public List<ChatContactVO> getContacts(Long currentUserId) {
        List<Map<String, Object>> latestMessages = chatMessageMapper.selectLatestMessageWithEachContact(currentUserId);
        if (latestMessages.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> contactIds = latestMessages.stream()
                .map(m -> {
                    Object val = m.get("contactId");
                    if (val == null) {
                        val = m.get("contactid");
                    }
                    return ((Number) val).longValue();
                })
                .collect(Collectors.toList());

        List<User> users = userMapper.selectBatchIds(contactIds);
        Map<Long, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }

        List<ChatContactVO> result = new ArrayList<>();
        for (Map<String, Object> msg : latestMessages) {
            Object contactIdVal = msg.get("contactId");
            if (contactIdVal == null) {
                contactIdVal = msg.get("contactid");
            }
            Long contactId = ((Number) contactIdVal).longValue();
            User contact = userMap.get(contactId);
            if (contact == null) {
                continue;
            }

            Object lastTimeVal = msg.get("lastTime");
            if (lastTimeVal == null) {
                lastTimeVal = msg.get("lasttime");
            }

            ChatContactVO vo = new ChatContactVO();
            vo.setContactId(contactId);
            vo.setNickname(contact.getNickname() != null ? contact.getNickname() : contact.getUsername());
            vo.setAvatar(contact.getAvatar());
            vo.setLastContent((String) msg.get("lastContent"));
            vo.setLastTime(lastTimeVal != null ? ((Number) lastTimeVal).longValue() : null);

            Integer unreadCount = chatMessageMapper.countUnreadFromContact(currentUserId, contactId);
            vo.setUnreadCount(unreadCount != null ? unreadCount : 0);

            result.add(vo);
        }

        result.sort((a, b) -> {
            Long t1 = a.getLastTime() != null ? a.getLastTime() : 0L;
            Long t2 = b.getLastTime() != null ? b.getLastTime() : 0L;
            return Long.compare(t2, t1);
        });

        return result;
    }

    @Override
    public List<ChatOrderConversationVO> getContactOrderConversations(Long currentUserId, Long contactId) {
        List<Map<String, Object>> latestMessages = chatMessageMapper.selectLatestMessageWithContactByOrder(currentUserId, contactId);
        if (latestMessages.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> orderIds = latestMessages.stream()
                .map(m -> {
                    Object val = m.get("orderId");
                    if (val == null) {
                        val = m.get("orderid");
                    }
                    return ((Number) val).longValue();
                })
                .collect(Collectors.toList());

        List<Order> orders = orderMapper.selectBatchIds(orderIds);
        Map<Long, Order> orderMap = new HashMap<>();
        for (Order order : orders) {
            orderMap.put(order.getId(), order);
        }

        List<ChatOrderConversationVO> result = new ArrayList<>();
        for (Map<String, Object> msg : latestMessages) {
            Object orderIdVal = msg.get("orderId");
            if (orderIdVal == null) {
                orderIdVal = msg.get("orderid");
            }
            Long orderId = ((Number) orderIdVal).longValue();
            Order order = orderMap.get(orderId);
            if (order == null) {
                continue;
            }

            Object lastTimeVal = msg.get("lastTime");
            if (lastTimeVal == null) {
                lastTimeVal = msg.get("lasttime");
            }

            ChatOrderConversationVO vo = new ChatOrderConversationVO();
            vo.setOrderId(orderId);
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderStatus(order.getStatus());
            vo.setOrderTitle(order.getItemDescription());
            vo.setLastContent((String) msg.get("lastContent"));
            vo.setLastTime(lastTimeVal != null ? ((Number) lastTimeVal).longValue() : null);

            Integer unreadCount = chatMessageMapper.countUnreadFromContactInOrder(currentUserId, contactId, orderId);
            vo.setUnreadCount(unreadCount != null ? unreadCount : 0);

            result.add(vo);
        }

        result.sort((a, b) -> {
            Long t1 = a.getLastTime() != null ? a.getLastTime() : 0L;
            Long t2 = b.getLastTime() != null ? b.getLastTime() : 0L;
            return Long.compare(t2, t1);
        });

        return result;
    }

    @Override
    public List<ChatMessageVO> getOrderMessages(Long currentUserId, Long orderId, Long lastMessageId, Integer pageSize) {
        ensureChatSession(currentUserId, orderId);
        int size = (pageSize == null || pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;

        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getOrderId, orderId)
                .orderByAsc(ChatMessage::getSendTime);
        if (lastMessageId != null) {
            wrapper.lt(ChatMessage::getId, lastMessageId);
        }
        wrapper.last("limit " + size);

        List<ChatMessage> list = chatMessageMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        // 将当前用户作为接收方的未读消息标记为已读
        chatMessageMapper.update(null,
                new LambdaUpdateWrapper<ChatMessage>()
                        .eq(ChatMessage::getOrderId, orderId)
                        .eq(ChatMessage::getToUserId, currentUserId)
                        .eq(ChatMessage::getReadStatus, 0)
                        .set(ChatMessage::getReadStatus, 1)
                        .set(ChatMessage::getReadTime, System.currentTimeMillis())
        );

        return list.stream().map(msg -> {
            ChatMessageVO vo = new ChatMessageVO();
            BeanUtils.copyProperties(msg, vo);
            vo.setSelf(msg.getFromUserId() != null && msg.getFromUserId().equals(currentUserId));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public ChatMessageVO saveMessage(Long fromUserId, Long orderId, String content) {
        return saveMessage(fromUserId, orderId, content, 1);
    }

    @Override
    public ChatMessageVO saveMessage(Long fromUserId, Long orderId, String content, Integer msgType) {
        ChatOrderRel rel = ensureChatSession(fromUserId, orderId);
        Long toUserId = rel.getUserId().equals(fromUserId) ? rel.getRunnerId() : rel.getUserId();
        if (toUserId == null) {
            throw new BusinessException("聊天对象不存在");
        }

        long now = System.currentTimeMillis();

        ChatMessage message = new ChatMessage();
        message.setOrderId(orderId);
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setMsgType(msgType);
        message.setReadStatus(0);
        message.setSendTime(now);
        message.setDeleted(0);

        chatMessageMapper.insert(message);

        // 获取发送者信息
        User fromUser = userMapper.selectById(fromUserId);
        String fromUserName = "用户";
        if (fromUser != null) {
            fromUserName = fromUser.getNickname() != null ? fromUser.getNickname() : fromUser.getPhone();
        }

        // 推送聊天消息通知给接收方（类型3：聊天消息）
        String notificationTitle = "新消息";
        String notificationContent = "您有一条来自 " + fromUserName + " 的新消息，点击查看";
        messageService.pushMessage(toUserId, notificationTitle, notificationContent, 3, orderId);

        log.info("聊天消息已发送，fromUserId={}, toUserId={}, orderId={}", fromUserId, toUserId, orderId);

        ChatMessageVO vo = new ChatMessageVO();
        BeanUtils.copyProperties(message, vo);
        vo.setSelf(true);
        return vo;
    }

    @Override
    public ChatPeerInfoVO getPeerInfo(Long currentUserId, Long orderId) {
        ChatOrderRel rel = ensureChatSession(currentUserId, orderId);
        Long peerId = rel.getUserId().equals(currentUserId) ? rel.getRunnerId() : rel.getUserId();
        if (peerId == null) {
            throw new BusinessException("聊天对象不存在");
        }
        User peer = userMapper.selectById(peerId);
        if (peer == null) {
            throw new BusinessException("聊天对象不存在");
        }

        ChatPeerInfoVO vo = new ChatPeerInfoVO();
        vo.setOrderId(orderId);
        vo.setPeerUserId(peerId);
        vo.setPeerNickname(peer.getNickname() != null ? peer.getNickname() : peer.getUsername());
        vo.setPeerAvatar(peer.getAvatar());
        return vo;
    }

    @Override
    public Integer getChatUnreadCount(Long currentUserId) {
        return chatMessageMapper.selectCount(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getToUserId, currentUserId)
                        .eq(ChatMessage::getReadStatus, 0)
        ).intValue();
    }
}

