package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表VO（管理员）
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class OrderListVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 订单类型ID
     */
    private Long orderTypeId;

    /**
     * 订单类型名称
     */
    private String orderTypeName;

    /**
     * 取件地址
     */
    private String pickupAddress;

    /**
     * 收件地址
     */
    private String deliveryAddress;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 订单状态
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
     * 跑腿员昵称
     */
    private String runnerNickname;

    /**
     * 跑腿员手机号
     */
    private String runnerPhone;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;
}
