package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录VO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class WithdrawalRecordVO {

    /**
     * 提现记录ID
     */
    private Long id;

    /**
     * 用户昵称（管理端展示用）
     */
    private String userNickname;

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
     * 账户类型文本
     */
    private String accountTypeText;

    /**
     * 账户信息
     */
    private String accountInfo;

    /**
     * 提现状态：1-待审核 2-已通过 3-已驳回 4-已到账
     */
    private Integer status;

    /**
     * 提现状态文本
     */
    private String statusText;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    /**
     * 转账时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transferTime;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
