package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 提现审核 DTO
 *
 * @author 周政
 * @date 2026-02-13
 */
@Data
public class WithdrawalAuditDTO {

    /**
     * 提现记录ID
     */
    @NotNull(message = "提现记录ID不能为空")
    private Long id;

    /**
     * 审核结果：2-已通过 3-已驳回
     */
    @NotNull(message = "审核结果不能为空")
    private Integer status;

    /**
     * 驳回原因（驳回时必填）
     */
    private String rejectReason;
}

