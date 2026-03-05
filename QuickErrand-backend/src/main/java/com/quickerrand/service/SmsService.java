package com.quickerrand.service;

/**
 * 短信Service接口
 *
 * @author 周政
 * @date 2026-01-26
 */
public interface SmsService {

    /**
     * 发送验证码
     */
    void sendCode(String phone);

    /**
     * 验证验证码
     */
    boolean verifyCode(String phone, String code);

}
