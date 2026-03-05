package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信登录DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class WxLoginDTO {

    /**
     * 微信授权code
     */
    @NotBlank(message = "微信授权code不能为空")
    private String code;

    /**
     * 手机号（可选，用于绑定）
     */
    private String phone;

    /**
     * 验证码（绑定手机号时需要）
     */
    private String smsCode;

}
