package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 地址实体类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_address")
public class Address extends BaseEntity {

    /**
     * 禁用逻辑删除（t_address 表没有 deleted 字段）
     */
    @TableField(exist = false)
    private Integer deleted;

    /**
     * 用户ID
     */
    private Long userId;

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
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 是否默认地址（0否1是）
     */
    private Integer isDefault;

}
