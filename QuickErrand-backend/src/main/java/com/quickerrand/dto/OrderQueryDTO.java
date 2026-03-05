package com.quickerrand.dto;

import lombok.Data;

/**
 * 订单查询DTO（管理员）
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class OrderQueryDTO {

    /**
     * 搜索关键词（订单号、用户手机号、地址）
     */
    private String keyword;

    /**
     * 订单状态：0-待支付 1-待接单 2-待取件 3-配送中 4-已完成 5-已取消
     */
    private Integer status;

    /**
     * 订单类型ID
     */
    private Long orderTypeId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 跑腿员ID
     */
    private Long runnerId;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
