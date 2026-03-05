package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 修改密码DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class ChangePasswordDTO {

    /**
     * 旧密码（若用户无密码则可为空）
     */
    private String oldPassword;

    /**
     * 新密码
     */
    @Pattern(regexp = "^.{6,20}$", message = "密码长度必须在6-20位之间")
    private String newPassword;
}
