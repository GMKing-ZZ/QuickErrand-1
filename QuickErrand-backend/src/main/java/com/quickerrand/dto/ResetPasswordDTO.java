package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 重置密码DTO
 *
 * @author 周政
 * @date 2026-03-04
 */
@Data
public class ResetPasswordDTO {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

}
