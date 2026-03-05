package com.quickerrand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.entity.Message;
import com.quickerrand.entity.User;
import com.quickerrand.mapper.MessageMapper;
import com.quickerrand.mapper.UserMapper;
import com.quickerrand.service.MessageService;
import com.quickerrand.vo.MessageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员消息管理控制器
 *
 * @author 周政
 * @date 2026-03-04
 */
@Slf4j
@Api(tags = "管理员消息管理接口")
@RestController
@RequestMapping("/admin/message")
public class AdminMessageController {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageService messageService;

    @ApiOperation("分页获取消息列表")
    @GetMapping("/page")
    public Result<Page<MessageVO>> getMessagePage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("消息类型") @RequestParam(required = false) Integer type,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        
        if (type != null) {
            wrapper.eq(Message::getType, type);
        }
        
        if (userId != null) {
            wrapper.eq(Message::getUserId, userId);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like(Message::getTitle, keyword)
                    .or().like(Message::getContent, keyword)
            );
        }
        
        wrapper.orderByDesc(Message::getCreateTime);

        Page<Message> page = new Page<>(pageNum, pageSize);
        messageMapper.selectPage(page, wrapper);

        Page<MessageVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<MessageVO> voList = page.getRecords().stream().map(message -> {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(message, vo);
            vo.setTypeText(getTypeText(message.getType()));
            
            if (message.getUserId() != null) {
                User user = userMapper.selectById(message.getUserId());
                if (user != null) {
                    vo.setUserName(user.getNickname() != null ? user.getNickname() : user.getPhone());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return Result.success(voPage);
    }

    @ApiOperation("发送系统消息给所有用户")
    @PostMapping("/send-all")
    public Result<Long> sendSystemMessageToAll(@RequestBody MessageDTO messageDTO) {
        Long messageId = messageService.sendSystemMessage(
                messageDTO.getTitle(), 
                messageDTO.getContent(), 
                messageDTO.getRelatedId()
        );
        return Result.success(messageId);
    }

    @ApiOperation("发送系统消息给指定用户")
    @PostMapping("/send-user")
    public Result<Void> sendSystemMessageToUser(@RequestBody MessageDTO messageDTO) {
        if (messageDTO.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        
        messageService.pushMessage(
                messageDTO.getUserId(),
                messageDTO.getTitle(),
                messageDTO.getContent(),
                2,
                messageDTO.getRelatedId()
        );
        return Result.success();
    }

    @ApiOperation("发送系统消息给多个用户")
    @PostMapping("/send-users")
    public Result<Void> sendSystemMessageToUsers(@RequestBody MessageDTO messageDTO) {
        if (messageDTO.getUserIds() == null || messageDTO.getUserIds().isEmpty()) {
            return Result.error("用户ID列表不能为空");
        }
        
        messageService.pushMessageToUsers(
                messageDTO.getUserIds(),
                messageDTO.getTitle(),
                messageDTO.getContent(),
                2,
                messageDTO.getRelatedId()
        );
        return Result.success();
    }

    @ApiOperation("删除消息")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        messageMapper.deleteById(id);
        return Result.success();
    }

    @ApiOperation("批量删除消息")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteMessages(@RequestBody List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            messageMapper.deleteBatchIds(ids);
        }
        return Result.success();
    }

    @ApiOperation("获取消息统计")
    @GetMapping("/stats")
    public Result<MessageStatsVO> getMessageStats() {
        MessageStatsVO stats = new MessageStatsVO();
        
        stats.setTotalCount(messageMapper.selectCount(null).intValue());
        stats.setUnreadCount(messageMapper.selectCount(
                new LambdaQueryWrapper<Message>().eq(Message::getIsRead, 0)
        ).intValue());
        stats.setOrderMessageCount(messageMapper.selectCount(
                new LambdaQueryWrapper<Message>().eq(Message::getType, 1)
        ).intValue());
        stats.setSystemMessageCount(messageMapper.selectCount(
                new LambdaQueryWrapper<Message>().eq(Message::getType, 2)
        ).intValue());
        stats.setChatMessageCount(messageMapper.selectCount(
                new LambdaQueryWrapper<Message>().eq(Message::getType, 3)
        ).intValue());
        
        return Result.success(stats);
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

    @lombok.Data
    public static class MessageDTO {
        private Long userId;
        private List<Long> userIds;
        private String title;
        private String content;
        private Long relatedId;
    }

    @lombok.Data
    public static class MessageStatsVO {
        private Integer totalCount;
        private Integer unreadCount;
        private Integer orderMessageCount;
        private Integer systemMessageCount;
        private Integer chatMessageCount;
    }
}
