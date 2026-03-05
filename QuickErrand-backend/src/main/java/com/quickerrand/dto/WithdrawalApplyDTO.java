package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提现申请DTO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class WithdrawalApplyDTO {

    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01", message = "提现金额必须大于0")
    private BigDecimal amount;

    /**
     * 账户类型：1-支付宝 2-微信 3-银行卡
     */
    @NotNull(message = "账户类型不能为空")
    private Integer accountType;

    /**
     * 账户信息（JSON格式）
     */
    @NotBlank(message = "账户信息不能为空")
    private String accountInfo;
}
