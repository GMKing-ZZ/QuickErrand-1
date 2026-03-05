package com.quickerrand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 跑腿员认证申请DTO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class RunnerAuthApplyDTO {

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$",
             message = "身份证号格式不正确")
    private String idCard;

    /**
     * 身份证正面照URL
     */
    @NotBlank(message = "身份证正面照不能为空")
    private String idCardFront;

    /**
     * 身份证反面照URL
     */
    @NotBlank(message = "身份证反面照不能为空")
    private String idCardBack;

}
