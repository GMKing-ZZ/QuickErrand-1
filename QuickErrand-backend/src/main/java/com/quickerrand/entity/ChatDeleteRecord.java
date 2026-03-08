package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聊天删除记录实体
 * 记录用户删除联系人或订单会话的时间点
 * 删除后，该用户无法看到删除前的聊天记录
 *
 * @author 周政
 * @date 2026-03-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_chat_delete_record")
public class ChatDeleteRecord extends BaseEntity {

    /**
     * 禁用逻辑删除
     */
    @TableField(exist = false)
    private Integer deleted;

    /**
     * 用户ID（执行删除操作的用户）
     */
    private Long userId;

    /**
     * 联系人ID
     */
    private Long contactId;

    /**
     * 订单ID（可选，删除特定订单会话时使用）
     */
    private Long orderId;

    /**
     * 删除时间戳（毫秒）
     */
    private Long deleteTime;
}
