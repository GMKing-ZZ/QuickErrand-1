package com.quickerrand.service.impl;

import com.quickerrand.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短信Service实现类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendCode(String phone) {
        // 本地开发环境：不实际发送验证码，也不使用Redis
        log.info("本地环境：模拟发送验证码成功，手机号：{}", phone);
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        // 本地开发环境：跳过验证码验证，任何非空验证码都能通过
        if (code != null && !code.isEmpty()) {
            log.info("本地环境：验证码验证通过，手机号：{}，输入验证码：{}", phone, code);
            return true;
        }
        
        log.warn("验证码为空，手机号：{}", phone);
        return false;
    }

}
