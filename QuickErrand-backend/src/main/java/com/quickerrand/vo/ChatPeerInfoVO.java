package com.quickerrand.vo;

import lombok.Data;

/**
 * 聊天对象基础信息 VO
 *
 * 用于会话页面顶部展示对端昵称、头像
 *
 * @author 周政
 * @date 2026-02-10
 */
@Data
public class ChatPeerInfoVO {

    /**
     * 订单ID
     */
    private Long orderId;

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
}

