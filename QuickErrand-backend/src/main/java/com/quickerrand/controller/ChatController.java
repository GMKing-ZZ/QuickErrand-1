package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.service.ChatService;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.ChatContactVO;
import com.quickerrand.vo.ChatConversationVO;
import com.quickerrand.vo.ChatMessageVO;
import com.quickerrand.vo.ChatOrderConversationVO;
import com.quickerrand.vo.ChatPeerInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import java.util.List;

/**
 * 聊天会话与消息接口
 *
 * @author 周政
 * @date 2026-02-10
 */
@Slf4j
@Api(tags = "聊天消息接口")
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Value("${file.upload.path:d:/upload/quickerrand}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:http://localhost:8088/api}")
    private String baseUrl;

    @ApiOperation("获取当前用户的联系人列表")
    @GetMapping("/contacts")
    public Result<List<ChatContactVO>> getContacts() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ChatContactVO> list = chatService.getContacts(userId);
        return Result.success(list);
    }

    @ApiOperation("获取与指定联系人的所有订单聊天列表")
    @GetMapping("/contact-conversations")
    public Result<List<ChatOrderConversationVO>> getContactConversations(@RequestParam("contactId") Long contactId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ChatOrderConversationVO> list = chatService.getContactOrderConversations(userId, contactId);
        return Result.success(list);
    }

    @ApiOperation("获取当前用户的聊天会话列表（旧接口，保留兼容）")
    @GetMapping("/conversations")
    public Result<List<ChatConversationVO>> getConversations() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ChatConversationVO> list = chatService.getUserConversations(userId);
        return Result.success(list);
    }

    @ApiOperation("获取指定订单下的聊天消息列表")
    @GetMapping("/messages")
    public Result<List<ChatMessageVO>> getMessages(@RequestParam("orderId") Long orderId,
                                                   @RequestParam(value = "lastMessageId", required = false) Long lastMessageId,
                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ChatMessageVO> list = chatService.getOrderMessages(userId, orderId, lastMessageId, pageSize);
        return Result.success(list);
    }

    @ApiOperation("获取聊天对象基础信息")
    @GetMapping("/peer-info")
    public Result<ChatPeerInfoVO> getPeerInfo(@RequestParam("orderId") Long orderId) {
        Long userId = SecurityUtils.getCurrentUserId();
        ChatPeerInfoVO info = chatService.getPeerInfo(userId, orderId);
        return Result.success(info);
    }

    @ApiOperation("上传聊天图片")
    @PostMapping("/upload")
    public Result<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("orderId") Long orderId) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : ".jpg";
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

            String relativePath = "chat/" + orderId + "/" + fileName;
            File destFile = new File(uploadPath + "/" + relativePath);
            File parentDir = destFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            file.transferTo(destFile);

            String imageUrl = baseUrl + "/uploads/" + relativePath;
            log.info("聊天图片上传成功，订单ID: {}, 文件路径: {}", orderId, imageUrl);
            return Result.success(imageUrl);
        } catch (IOException e) {
            log.error("上传聊天图片失败", e);
            return Result.error("上传失败，请稍后重试");
        }
    }

    @ApiOperation("删除联系人聊天记录")
    @DeleteMapping("/contact/{contactId}")
    public Result<Void> deleteContact(@PathVariable Long contactId) {
        Long userId = SecurityUtils.getCurrentUserId();
        chatService.deleteContact(userId, contactId);
        return Result.success();
    }

    @ApiOperation("删除订单会话聊天记录")
    @DeleteMapping("/order-conversation/{contactId}/{orderId}")
    public Result<Void> deleteOrderConversation(@PathVariable Long contactId, @PathVariable Long orderId) {
        Long userId = SecurityUtils.getCurrentUserId();
        chatService.deleteOrderConversation(userId, contactId, orderId);
        return Result.success();
    }
}

