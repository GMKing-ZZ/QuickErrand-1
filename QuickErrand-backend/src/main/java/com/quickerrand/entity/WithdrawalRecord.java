package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_withdrawal_record")
public class WithdrawalRecord extends BaseEntity {

    /**
     * 禁用逻辑删除（t_withdrawal_record 表没有 deleted 字段）
     */
    @TableField(exist = false)
    private Integer deleted;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 实际到账金额
     */
    private BigDecimal actualAmount;

    /**
     * 账户类型：1-支付宝 2-微信 3-银行卡
     */
    private Integer accountType;

    /**
     * 账户信息（JSON格式存储）
     */
    private String accountInfo;

    /**
     * 提现状态：1-待审核 2-已通过 3-已驳回 4-已到账
     */
    private Integer status;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 转账时间
     */
    private LocalDateTime transferTime;

    /**
     * 驳回原因
     */
    private String rejectReason;
}
