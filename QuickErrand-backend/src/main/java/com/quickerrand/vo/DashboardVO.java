package com.quickerrand.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 数据看板VO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
@ApiModel("数据看板VO")
public class DashboardVO {

    @ApiModelProperty("总用户数")
    private Long totalUsers;

    @ApiModelProperty("总订单数")
    private Long totalOrders;

    @ApiModelProperty("总跑腿员数")
    private Long totalRunners;

    @ApiModelProperty("总收入")
    private BigDecimal totalRevenue;

    @ApiModelProperty("总平台服务费")
    private BigDecimal totalPlatformFee;

    @ApiModelProperty("今日新增用户数")
    private Long todayNewUsers;

    @ApiModelProperty("今日新增订单数")
    private Long todayNewOrders;

    @ApiModelProperty("今日新增跑腿员数")
    private Long todayNewRunners;

    @ApiModelProperty("今日收入")
    private BigDecimal todayRevenue;

    @ApiModelProperty("收入周同比（百分比，正数表示增长，负数表示下降）")
    private BigDecimal revenueWeekRatio;

    @ApiModelProperty("收入日同比（百分比，正数表示增长，负数表示下降）")
    private BigDecimal revenueDayRatio;

    @ApiModelProperty("用户数周同比（百分比，正数表示增长，负数表示下降）")
    private BigDecimal usersWeekRatio;

    @ApiModelProperty("用户数日同比（百分比，正数表示增长，负数表示下降）")
    private BigDecimal usersDayRatio;

    @ApiModelProperty("订单数周同比（百分比，正数表示增长，负数表示下降）")
    private BigDecimal ordersWeekRatio;

    @ApiModelProperty("订单数日同比（百分比，正数表示增长，负数表示下降）")
    private BigDecimal ordersDayRatio;

    @ApiModelProperty("跑腿员数周同比（百分比，正数表示增长，负数表示下降）")
    private BigDecimal runnersWeekRatio;

    @ApiModelProperty("跑腿员数日同比（百分比，正数表示增长，负数表示下降）")
    private BigDecimal runnersDayRatio;

    @ApiModelProperty("订单状态统计")
    private List<OrderStatusStatistics> orderStatusStatistics;

    @ApiModelProperty("最近7天订单趋势")
    private List<DailyStatistics> orderTrend;

    @ApiModelProperty("最近7天收入趋势")
    private List<DailyStatistics> revenueTrend;

    @ApiModelProperty("订单类型统计")
    private List<OrderTypeStatistics> orderTypeStatistics;

    /**
     * 订单状态统计
     */
    @Data
    public static class OrderStatusStatistics {
        @ApiModelProperty("状态名称")
        private String statusName;

        @ApiModelProperty("订单数量")
        private Long count;
    }

    /**
     * 每日统计
     */
    @Data
    public static class DailyStatistics {
        @ApiModelProperty("日期")
        private String date;

        @ApiModelProperty("数值")
        private BigDecimal value;
    }

    /**
     * 订单类型统计
     */
    @Data
    public static class OrderTypeStatistics {
        @ApiModelProperty("类型名称")
        private String typeName;

        @ApiModelProperty("订单数量")
        private Long count;
    }
}
