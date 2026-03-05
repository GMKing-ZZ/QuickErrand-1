package com.quickerrand.vo;

import lombok.Data;

/**
 * 聊天会话列表项 VO
 *
 * 用于会话列表页面展示
 *
 * @author 周政
 * @date 2026-02-10
 */
@Data
public class ChatConversationVO {

    /**
     * 订单ID（会话唯一标识）
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 对端用户ID
     */
    private Long peerUserId;

    /**
     * 对端昵称
     */
    private String peerNickname;

    /**
     * 对端头像
     */
    private String peerAvatar;

    /**
     * 最近一条消息内容
     */
    private String lastContent;

    /**
     * 最近一条消息发送时间戳（毫秒）
     */
    private Long lastTime;

    /**
     * 未读消息数量
     */
    private Integer unreadCount;
}

