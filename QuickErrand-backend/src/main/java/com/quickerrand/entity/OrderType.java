package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单类型实体类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_type")
public class OrderType extends BaseEntity {

    /**
     * 禁用逻辑删除（t_order_type 表没有 deleted 字段）
     */
    @TableField(exist = false)
    private Integer deleted;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型图标
     */
    @TableField("type_icon")
    private String typeIcon;

    /**
     * 类型描述
     */
    @TableField("type_desc")
    private String typeDesc;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态（0禁用1启用）
     */
    private Integer status;

}
