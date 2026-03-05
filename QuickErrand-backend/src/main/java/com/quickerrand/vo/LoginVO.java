package com.quickerrand.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /**
     * Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserVO userInfo;

}
