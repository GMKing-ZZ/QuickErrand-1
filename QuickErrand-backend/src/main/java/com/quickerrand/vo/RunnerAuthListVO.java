package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 跑腿员认证列表VO（管理员）
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
public class RunnerAuthListVO {

    /**
     * 跑腿员信息ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 身份证正面照
     */
    private String idCardFront;

    /**
     * 身份证反面照
     */
    private String idCardBack;

    /**
     * 认证状态（0未认证1审核中2已认证3已驳回）
     */
    private Integer certStatus;

    /**
     * 认证状态文本
     */
    private String certStatusText;

    /**
     * 认证时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime certTime;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 创建时间（申请时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
