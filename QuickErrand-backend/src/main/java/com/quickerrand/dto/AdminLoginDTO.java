package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 管理员登录DTO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class AdminLoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
