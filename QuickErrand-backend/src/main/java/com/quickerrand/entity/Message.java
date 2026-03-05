package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息通知实体
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_message")
public class Message extends BaseEntity {

    /**
     * 禁用逻辑删除（t_message 表没有 deleted 字段）
     */
    @TableField(exist = false)
    private Integer deleted;

    /**
     * 用户ID
     */
    private Long userId;

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
     * 关联ID（订单ID等）
     */
    private Long relatedId;

    /**
     * 是否已读：0-未读 1-已读
     */
    private Integer isRead;
}
