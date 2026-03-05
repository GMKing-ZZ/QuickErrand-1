package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseEntity {

    /**
     * 禁用逻辑删除（t_order 表没有 deleted 字段）
     */
    @TableField(exist = false)
    private Integer deleted;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 跑腿员ID
     */
    private Long runnerId;

    /**
     * 订单类型ID
     */
    private Long orderTypeId;

    /**
     * 取货联系人
     */
    private String pickupContact;

    /**
     * 取货联系电话
     */
    private String pickupPhone;

    /**
     * 取货地址
     */
    private String pickupAddress;

    /**
     * 取货经度
     */
    private BigDecimal pickupLongitude;

    /**
     * 取货纬度
     */
    private BigDecimal pickupLatitude;

    /**
     * 收货地址
     */
    private String deliveryAddress;

    /**
     * 收货联系人
     */
    private String deliveryContact;

    /**
     * 收货联系电话
     */
    private String deliveryPhone;

    /**
     * 收货经度
     */
    private BigDecimal deliveryLongitude;

    /**
     * 收货纬度
     */
    private BigDecimal deliveryLatitude;

    /**
     * 距离（米）
     */
    private Integer distance;

    /**
     * 物品描述
     */
    private String itemDescription;

    /**
     * 物品图片（JSON数组）
     */
    private String itemImages;

    /**
     * 预期完成时间
     */
    private LocalDateTime expectedTime;

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
     * 订单状态（1待支付2待接单3服务中4已完成5已取消）
     */
    private Integer status;

    /**
     * 支付方式（1微信支付2余额支付）
     */
    private Integer paymentMethod;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /**
     * 接单时间
     */
    private LocalDateTime acceptTime;

    /**
     * 取货时间
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
     * 取消原因
     */
    private String cancelReason;

}
