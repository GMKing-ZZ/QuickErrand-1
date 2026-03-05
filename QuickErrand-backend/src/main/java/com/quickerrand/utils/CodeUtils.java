package com.quickerrand.utils;

import java.util.Random;

/**
 * 验证码工具类
 *
 * @author 周政
 * @date 2026-01-26
 */
public class CodeUtils {

    private static final Random RANDOM = new Random();

    /**
     * 生成6位数字验证码
     */
    public static String generateCode() {
        return generateCode(6);
    }

    /**
     * 生成指定位数的数字验证码
     */
    public static String generateCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(RANDOM.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 生成收货码（8位数字+字母）
     */
    public static String generatePickupCode() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return code.toString();
    }

}
