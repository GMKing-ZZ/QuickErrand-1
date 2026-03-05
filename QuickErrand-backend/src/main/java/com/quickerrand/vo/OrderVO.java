package com.quickerrand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单VO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class OrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单类型ID
     */
    private Long orderTypeId;

    /**
     * 订单类型名称
     */
    private String orderTypeName;

    /**
     * 取件联系人
     */
    private String pickupContact;

    /**
     * 取件联系电话
     */
    private String pickupPhone;

    /**
     * 取件详细地址
     */
    private String pickupAddress;

    /**
     * 收件联系人
     */
    private String deliveryContact;

    /**
     * 收件联系电话
     */
    private String deliveryPhone;

    /**
     * 收件详细地址
     */
    private String deliveryAddress;

    /**
     * 距离（公里）
     */
    private BigDecimal distance;

    /**
     * 物品描述
     */
    private String itemDescription;

    /**
     * 物品图片（JSON数组字符串）
     */
    private String itemImages;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 服务费用
     */
    private BigDecimal serviceFee;

    /**
     * 平台费用
     */
    private BigDecimal platformFee;

    /**
     * 跑腿员费用
     */
    private BigDecimal runnerFee;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收货码
     */
    private String pickupCode;

    /**
     * 订单状态（0待支付1待接单2待取件3配送中4已完成5已取消）
     */
    private Integer status;

    /**
     * 订单状态文本
     */
    private String statusText;

    /**
     * 跑腿员ID
     */
    private Long runnerId;

    /**
     * 跑腿员姓名
     */
    private String runnerName;

    /**
     * 跑腿员电话
     */
    private String runnerPhone;

    /**
     * 支付方式（1微信支付2余额支付）
     */
    private Integer paymentMethod;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 接单时间
     */
    private LocalDateTime acceptTime;

    /**
     * 取件时间
     */
    private LocalDateTime pickupTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 用户是否已评价该订单
     */
    private Boolean evaluated;

    /**
     * 评价服务质量评分（1-5）
     */
    private Integer serviceScore;

    /**
     * 评价服务态度评分（1-5）
     */
    private Integer attitudeScore;

    /**
     * 评价内容
     */
    private String evaluationContent;

    /**
     * 评价图片（JSON数组字符串）
     */
    private String evaluationImages;

}
