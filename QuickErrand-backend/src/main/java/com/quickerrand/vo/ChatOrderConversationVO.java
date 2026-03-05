package com.quickerrand.vo;

import lombok.Data;

/**
 * 订单会话列表项 VO
 *
 * 用于展示与某个联系人的不同订单的聊天
 *
 * @author 周政
 * @date 2026-03-04
 */
@Data
public class ChatOrderConversationVO {

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
     * 订单标题/描述
     */
    private String orderTitle;

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
