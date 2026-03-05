package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 管理员创建用户DTO
 *
 * @author 周政
 * @date 2026-01-31
 */
@Data
public class CreateUserDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 用户类型（1普通用户2跑腿员3管理员）
     */
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    /**
     * 状态（0禁用1正常）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别（0未知1男2女）
     */
    private Integer gender;

    /**
     * 生日
     */
    private String birthday;

}
