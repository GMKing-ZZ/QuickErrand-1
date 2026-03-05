package com.quickerrand.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地址VO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class AddressVO {

    /**
     * 地址ID
     */
    private Long id;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
