package com.quickerrand.dto;

import lombok.Data;

/**
 * 用户查询DTO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class UserQueryDTO {

    /**
     * 搜索关键词（用户名、昵称、手机号）
     */
    private String keyword;

    /**
     * 用户类型：1-普通用户 2-跑腿员 3-管理员
     */
    private Integer userType;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
