package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息通知VO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class MessageVO {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称（管理后台用）
     */
    private String userName;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：1-订单消息 2-系统消息 3-聊天消息
     */
    private Integer type;

    /**
     * 消息类型文本
     */
    private String typeText;

    /**
     * 关联ID（订单ID等）
     */
    private Long relatedId;

    /**
     * 是否已读：0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
