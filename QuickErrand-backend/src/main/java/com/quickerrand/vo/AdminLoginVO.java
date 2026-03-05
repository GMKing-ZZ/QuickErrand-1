package com.quickerrand.vo;

import lombok.Data;

/**
 * 管理员登录响应VO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class AdminLoginVO {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private AdminUserInfo userInfo;

    @Data
    public static class AdminUserInfo {
        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户名
         */
        private String username;

        /**
         * 昵称
         */
        private String nickname;

        /**
         * 用户类型
         */
        private Integer userType;

        /**
         * 头像URL
         */
        private String avatar;
    }
}
