package com.quickerrand.vo;

import lombok.Data;

/**
 * 聊天消息 VO
 *
 * 用于前端渲染单条聊天消息
 *
 * @author 周政
 * @date 2026-02-10
 */
@Data
public class ChatMessageVO {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 发送方用户ID
     */
    private Long fromUserId;

    /**
     * 接收方用户ID
     */
    private Long toUserId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型，1-文字，2-图片
     */
    private Integer msgType;

    /**
     * 已读状态：0-未读，1-已读
     */
    private Integer readStatus;

    /**
     * 发送时间戳（毫秒）
     */
    private Long sendTime;

    /**
     * 已读时间戳（毫秒）
     */
    private Long readTime;

    /**
     * 是否为当前登录用户发送
     */
    private Boolean self;
}

