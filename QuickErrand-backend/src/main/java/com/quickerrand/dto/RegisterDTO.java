package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户注册DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class RegisterDTO {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户类型（1普通用户 2跑腿员）
     * 如果为空或非法，后端将默认设置为普通用户
     */
    private Integer userType;

}
