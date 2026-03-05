package com.quickerrand.vo;

import lombok.Data;

/**
 * 联系人列表项 VO
 *
 * 用于消息页面展示联系人
 *
 * @author 周政
 * @date 2026-03-04
 */
@Data
public class ChatContactVO {

    /**
     * 联系人用户ID
     */
    private Long contactId;

    /**
     * 联系人昵称
     */
    private String nickname;

    /**
     * 联系人头像
     */
    private String avatar;

    /**
     * 最近一条消息内容
     */
    private String lastContent;

    /**
     * 最近一条消息发送时间戳（毫秒）
     */
    private Long lastTime;

    /**
     * 未读消息总数（与该联系人的所有订单聊天未读数之和）
     */
    private Integer unreadCount;
}
