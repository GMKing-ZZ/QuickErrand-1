package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收益明细VO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class EarningsDetailVO {

    /**
     * 收益记录ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 收益金额
     */
    private BigDecimal amount;

    /**
     * 收益类型：1-订单收益 2-奖励 3-提现
     */
    private Integer type;

    /**
     * 收益类型文本
     */
    private String typeText;

    /**
     * 结算状态：1-待结算 2-已结算
     */
    private Integer status;

    /**
     * 结算状态文本
     */
    private String statusText;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
