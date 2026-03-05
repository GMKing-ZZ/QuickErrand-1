package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 订单评价DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class EvaluateOrderDTO {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 服务质量评分（1-5分）
     */
    @NotNull(message = "服务质量评分不能为空")
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    private Integer serviceScore;

    /**
     * 服务态度评分（1-5分）
     */
    @NotNull(message = "服务态度评分不能为空")
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    private Integer attitudeScore;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片（JSON数组字符串）
     */
    private String images;

    /**
     * 评价标签（JSON数组字符串）
     */
    private String tags;

    /**
     * 评分（1-5分）- 兼容字段，如果提供了此字段，将同时设置服务质量和服务态度评分
     */
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    private Integer score;

}
