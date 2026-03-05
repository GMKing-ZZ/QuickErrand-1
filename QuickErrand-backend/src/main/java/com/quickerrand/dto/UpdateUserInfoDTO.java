package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 更新个人信息DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class UpdateUserInfoDTO {

    /**
     * 用户名
     */
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名格式不正确，需4-20位字母、数字或下划线")
    private String username;

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
     * 是否默认开启收货码（true开启false关闭）
     */
    private Boolean pickupCodeEnabled;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 验证码（修改手机号时必填）
     */
    private String code;
}
