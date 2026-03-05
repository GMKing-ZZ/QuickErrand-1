package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 地址DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class AddressDTO {

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /**
     * 联系人电话
     */
    @NotBlank(message = "联系人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * 区县
     */
    @NotBlank(message = "区县不能为空")
    private String district;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 是否默认地址（0否1是）
     */
    private Integer isDefault;
}
