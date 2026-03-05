package com.quickerrand.service;

import com.quickerrand.entity.ChatOrderRel;
import com.quickerrand.vo.ChatContactVO;
import com.quickerrand.vo.ChatConversationVO;
import com.quickerrand.vo.ChatMessageVO;
import com.quickerrand.vo.ChatOrderConversationVO;
import com.quickerrand.vo.ChatPeerInfoVO;

import java.util.List;

/**
 * 聊天会话与消息服务接口
 *
 * @author 周政
 * @date 2026-02-10
 */
public interface ChatService {

    /**
     * 确保当前用户与指定订单存在有效的聊天会话绑定关系
     * 如不存在则自动创建
     *
     * @param currentUserId 当前登录用户ID
     * @param orderId       订单ID
     * @return 会话绑定实体
     */
    ChatOrderRel ensureChatSession(Long currentUserId, Long orderId);

    /**
     * 获取当前用户的所有聊天会话列表
     *
     * @param currentUserId 当前登录用户ID
     * @return 会话列表
     */
    List<ChatConversationVO> getUserConversations(Long currentUserId);

    /**
     * 获取当前用户的联系人列表
     *
     * @param currentUserId 当前登录用户ID
     * @return 联系人列表
     */
    List<ChatContactVO> getContacts(Long currentUserId);

    /**
     * 获取与指定联系人的所有订单聊天列表
     *
     * @param currentUserId 当前登录用户ID
     * @param contactId     联系人ID
     * @return 订单会话列表
     */
    List<ChatOrderConversationVO> getContactOrderConversations(Long currentUserId, Long contactId);

    /**
     * 获取指定订单下的聊天消息列表（按时间升序）
     *
     * @param currentUserId  当前登录用户ID
     * @param orderId        订单ID
     * @param lastMessageId  上一次加载的最后一条消息ID（分页向上翻时使用，可为null）
     * @param pageSize       每次加载数量，默认 50
     * @return 消息列表
     */
    List<ChatMessageVO> getOrderMessages(Long currentUserId, Long orderId, Long lastMessageId, Integer pageSize);

    /**
     * 发送一条聊天消息并持久化
     *
     * @param fromUserId  发送方用户ID
     * @param orderId     订单ID
     * @param content     消息内容
     * @return 消息VO
     */
    ChatMessageVO saveMessage(Long fromUserId, Long orderId, String content);

    /**
     * 发送一条聊天消息并持久化（支持消息类型）
     *
     * @param fromUserId  发送方用户ID
     * @param orderId     订单ID
     * @param content     消息内容
     * @param msgType     消息类型，1-文字，2-图片
     * @return 消息VO
     */
    ChatMessageVO saveMessage(Long fromUserId, Long orderId, String content, Integer msgType);

    /**
     * 获取当前用户在指定订单下的聊天对象基础信息
     *
     * @param currentUserId 当前登录用户ID
     * @param orderId       订单ID
     * @return 聊天对象信息
     */
    ChatPeerInfoVO getPeerInfo(Long currentUserId, Long orderId);

    /**
     * 获取当前用户的聊天消息总未读数
     *
     * @param currentUserId 当前登录用户ID
     * @return 聊天消息未读数
     */
    Integer getChatUnreadCount(Long currentUserId);
}

