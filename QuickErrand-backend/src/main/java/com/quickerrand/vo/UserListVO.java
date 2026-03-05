package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户列表VO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class UserListVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户类型：1-普通用户 2-跑腿员 3-管理员
     */
    private Integer userType;

    /**
     * 用户类型文本
     */
    private String userTypeText;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
