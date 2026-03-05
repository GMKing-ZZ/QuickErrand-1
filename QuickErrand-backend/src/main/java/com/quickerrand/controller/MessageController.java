package com.quickerrand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.service.ChatService;
import com.quickerrand.service.MessageService;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.MessageVO;
import com.quickerrand.vo.UnreadCountVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息通知控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "消息通知接口")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatService chatService;

    @ApiOperation("获取消息列表")
    @GetMapping("/list")
    public Result<List<MessageVO>> getMessageList() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<MessageVO> messages = messageService.getUserMessages(userId);
        return Result.success(messages);
    }

    @ApiOperation("分页获取消息列表")
    @GetMapping("/page")
    public Result<Page<MessageVO>> getMessagePage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("消息类型") @RequestParam(required = false) Integer type) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<MessageVO> page = messageService.getUserMessagesPage(userId, pageNum, pageSize, type);
        return Result.success(page);
    }

    @ApiOperation("获取未读消息数量")
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }

    @ApiOperation("获取未读消息统计（系统消息+聊天消息）")
    @GetMapping("/unread-stats")
    public Result<UnreadCountVO> getUnreadStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer messageUnread = messageService.getUnreadCount(userId);
        Integer chatUnread = chatService.getChatUnreadCount(userId);
        
        UnreadCountVO vo = new UnreadCountVO();
        vo.setMessageUnread(messageUnread);
        vo.setChatUnread(chatUnread);
        vo.setTotalUnread(messageUnread + chatUnread);
        return Result.success(vo);
    }

    @ApiOperation("标记消息为已读")
    @PostMapping("/read/{messageId}")
    public Result<Void> markAsRead(@PathVariable Long messageId) {
        Long userId = SecurityUtils.getCurrentUserId();
        messageService.markAsRead(userId, messageId);
        return Result.success();
    }

    @ApiOperation("标记所有消息为已读")
    @PostMapping("/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        messageService.markAllAsRead(userId);
        return Result.success();
    }

    @ApiOperation("删除消息")
    @DeleteMapping("/{messageId}")
    public Result<Void> deleteMessage(@PathVariable Long messageId) {
        Long userId = SecurityUtils.getCurrentUserId();
        messageService.deleteMessage(userId, messageId);
        return Result.success();
    }
}
