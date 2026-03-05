package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 计算订单费用DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class CalculateFeeDTO {

    /**
     * 订单类型ID
     */
    @NotNull(message = "订单类型不能为空")
    private Long orderTypeId;

    /**
     * 距离（公里）
     */
    @NotNull(message = "距离不能为空")
    private BigDecimal distance;

}
