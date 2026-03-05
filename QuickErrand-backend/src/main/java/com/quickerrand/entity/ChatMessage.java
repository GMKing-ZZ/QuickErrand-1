package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聊天消息实体
 *
 * 对应表：chat_message
 *
 * @author 周政
 * @date 2026-02-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_chat_message")
public class ChatMessage extends BaseEntity {

    /**
     * 关联跑腿订单ID
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
     * 消息内容（已过滤敏感词）
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
     * 发送时间戳（毫秒，服务端时间）
     */
    private Long sendTime;

    /**
     * 已读时间戳（毫秒）
     */
    private Long readTime;

    /**
     * 覆盖父类的deleted字段，因为chat_message表没有此字段
     */
    @TableField(exist = false)
    private Integer deleted;
}

