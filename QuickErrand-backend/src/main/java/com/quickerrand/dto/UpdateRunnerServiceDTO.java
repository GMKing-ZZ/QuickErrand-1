package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 更新跑腿员服务设置DTO
 *
 * @author 周政
 * @date 2026-03-03
 */
@Data
public class UpdateRunnerServiceDTO {

    /**
     * 服务时间（格式：startTime-endTime，如：08:00-22:00）
     */
    @NotNull(message = "服务时间不能为空")
    private String serviceTime;

    /**
     * 服务范围（单位：米，范围：1000-20000）
     */
    @NotNull(message = "服务范围不能为空")
    @Min(value = 1000, message = "服务范围最小为1公里")
    @Max(value = 20000, message = "服务范围最大为20公里")
    private Integer serviceRange;
}
