package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端收益结算 VO
 *
 * @author 周政（扩展）
 * @date 2026-02-13
 */
@Data
public class EarningsAdminVO {

    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 收益金额
     */
    private BigDecimal amount;

    /**
     * 收益类型：1-订单收益 2-奖励 3-提现
     */
    private Integer type;

    /**
     * 收益类型文本
     */
    private String typeText;

    /**
     * 结算状态：1-待结算 2-已结算
     */
    private Integer status;

    /**
     * 结算状态文本
     */
    private String statusText;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

