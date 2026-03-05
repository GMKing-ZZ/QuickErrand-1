package com.quickerrand.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 *
 * @author 周政
 * @date 2026-01-27
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("未登录或登录已过期");
    }

    /**
     * 获取当前登录用户ID（不抛出异常）
     *
     * @return 用户ID，未登录返回null
     */
    public static Long getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Long;
    }
}
