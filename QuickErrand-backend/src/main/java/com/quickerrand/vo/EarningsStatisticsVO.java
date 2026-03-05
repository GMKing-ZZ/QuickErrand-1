package com.quickerrand.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 收益统计VO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class EarningsStatisticsVO {

    /**
     * 总收益
     */
    private BigDecimal totalEarnings;

    /**
     * 待结算收益
     */
    private BigDecimal pendingEarnings;

    /**
     * 已结算收益
     */
    private BigDecimal settledEarnings;

    /**
     * 可提现金额
     */
    private BigDecimal withdrawableAmount;

    /**
     * 今日收益
     */
    private BigDecimal todayEarnings;

    /**
     * 本月收益
     */
    private BigDecimal monthEarnings;

    /**
     * 订单收益总额
     */
    private BigDecimal orderEarnings;

    /**
     * 奖励总额
     */
    private BigDecimal bonusEarnings;
}
