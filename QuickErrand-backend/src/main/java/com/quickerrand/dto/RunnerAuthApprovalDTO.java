package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 跑腿员认证审核DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class RunnerAuthApprovalDTO {

    /**
     * 跑腿员信息ID
     */
    @NotNull(message = "跑腿员信息ID不能为空")
    private Long runnerInfoId;

    /**
     * 审核结果（2已认证3已驳回）
     */
    @NotNull(message = "审核结果不能为空")
    private Integer certStatus;

    /**
     * 驳回原因（驳回时必填）
     */
    private String rejectReason;

}
