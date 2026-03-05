package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单-聊天会话绑定实体
 *
 * 对应表：chat_order_rel
 *
 * @author 周政
 * @date 2026-02-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_chat_order_rel")
public class ChatOrderRel extends BaseEntity {

    /**
     * 跑腿订单ID
     */
    private Long orderId;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 跑腿员ID
     */
    private Long runnerId;

    /**
     * 软删除时间戳，null 表示有效
     */
    private LocalDateTime deleteTime;

    /**
     * 覆盖父类的deleted字段，因为t_chat_order_rel表没有此字段
     */
    @TableField(exist = false)
    private Integer deleted;
}

