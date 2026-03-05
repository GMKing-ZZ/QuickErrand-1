package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 跑腿员信用等级VO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
@ApiModel("跑腿员信用等级VO")
public class RunnerCreditVO {

    @ApiModelProperty("跑腿员ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("信用等级（1-5星）")
    private Integer creditLevel;

    @ApiModelProperty("完成订单数")
    private Integer totalOrders;

    @ApiModelProperty("好评率")
    private BigDecimal goodRate;

    @ApiModelProperty("认证时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("服务时间")
    private String serviceTime;

    @ApiModelProperty("服务范围（米）")
    private Integer serviceRange;
}
