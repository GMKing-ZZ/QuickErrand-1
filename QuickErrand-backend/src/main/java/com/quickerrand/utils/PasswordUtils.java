package com.quickerrand.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 密码工具类
 *
 * @author 周政
 * @date 2026-01-26
 */
public class PasswordUtils {

    /**
     * 生成盐值
     */
    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 加密密码
     */
    public static String encryptPassword(String password, String salt) {
        String saltedPassword = password + salt;
        return DigestUtils.md5DigestAsHex(saltedPassword.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 验证密码
     */
    public static boolean verifyPassword(String inputPassword, String salt, String encryptedPassword) {
        String encrypted = encryptPassword(inputPassword, salt);
        return encrypted.equals(encryptedPassword);
    }

}
