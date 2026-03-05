package com.quickerrand.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户信息VO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别（0未知1男2女）
     */
    private Integer gender;

    /**
     * 生日（格式：yyyy-MM-dd）
     */
    private String birthday;

    /**
     * 用户类型（1普通用户2跑腿员）
     */
    private Integer userType;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 是否默认开启收货码（0关闭1开启）
     */
    private Integer pickupCodeEnabled;

    /**
     * 是否设置了密码
     */
    private Boolean hasPassword;

}
