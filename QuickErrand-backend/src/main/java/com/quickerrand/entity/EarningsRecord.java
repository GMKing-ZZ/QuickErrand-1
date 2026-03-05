package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 收益记录实体
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_earnings_record")
public class EarningsRecord extends BaseEntity {

    /**
     * 禁用逻辑删除（t_earnings_record 表没有 deleted 字段）
     */
    @TableField(exist = false)
    private Integer deleted;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单ID（类型为订单收益时关联）
     */
    private Long orderId;

    /**
     * 收益金额
     */
    private BigDecimal amount;

    /**
     * 收益类型：1-订单收益 2-奖励 3-提现
     */
    private Integer type;

    /**
     * 结算状态：1-待结算 2-已结算
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
