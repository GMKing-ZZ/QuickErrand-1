package com.quickerrand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单类型VO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class OrderTypeVO {

    /**
     * 类型ID
     */
    private Long id;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型图标URL
     */
    private String icon;

    /**
     * 类型描述
     */
    private String description;

    /**
     * 基础价格
     */
    private BigDecimal basePrice;

    /**
     * 每公里价格
     */
    private BigDecimal pricePerKm;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0禁用1启用）
     */
    private Integer status;

}
