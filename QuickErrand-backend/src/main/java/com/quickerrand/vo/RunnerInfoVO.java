package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 跑腿员信息VO
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
public class RunnerInfoVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号（脱敏）
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
     * 信用等级（1-5星）
     */
    private Integer creditLevel;

    /**
     * 完成订单数
     */
    private Integer totalOrders;

    /**
     * 好评率
     */
    private BigDecimal goodRate;

    /**
     * 服务时间
     */
    private String serviceTime;

    /**
     * 服务范围（米）
     */
    private Integer serviceRange;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
