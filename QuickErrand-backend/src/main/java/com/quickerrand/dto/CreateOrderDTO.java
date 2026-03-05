package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建订单DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class CreateOrderDTO {

    /**
     * 订单类型ID
     */
    @NotNull(message = "订单类型不能为空")
    private Long orderTypeId;

    /**
     * 取件地址ID
     */
    private Long pickupAddressId;

    /**
     * 收件地址ID
     */
    private Long deliveryAddressId;

    /**
     * 取件联系人
     */
    @NotBlank(message = "取件联系人不能为空")
    private String pickupContact;

    /**
     * 取件联系电话
     */
    @NotBlank(message = "取件联系电话不能为空")
    private String pickupPhone;

    /**
     * 取件详细地址
     */
    @NotBlank(message = "取件地址不能为空")
    private String pickupAddress;

    /**
     * 取件经度
     */
    private String pickupLongitude;

    /**
     * 取件纬度
     */
    private String pickupLatitude;

    /**
     * 收件联系人
     */
    @NotBlank(message = "收件联系人不能为空")
    private String deliveryContact;

    /**
     * 收件联系电话
     */
    @NotBlank(message = "收件联系电话不能为空")
    private String deliveryPhone;

    /**
     * 收件详细地址
     */
    @NotBlank(message = "收件地址不能为空")
    private String deliveryAddress;

    /**
     * 收件经度
     */
    private String deliveryLongitude;

    /**
     * 收件纬度
     */
    private String deliveryLatitude;

    /**
     * 距离（公里）
     */
    private BigDecimal distance;

    /**
     * 物品描述
     */
    private String itemDescription;

    /**
     * 物品图片（JSON数组）
     */
    private String itemImages;

    /**
     * 自定义金额（选填，如果提供则使用此金额，否则使用计算出的金额）
     */
    private BigDecimal customAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 支付方式（1微信支付2余额支付）
     */
    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod;

    /**
     * 是否开启收货码（true开启false关闭）
     * 如果不传，则使用用户默认设置
     */
    private Boolean enablePickupCode;

}
