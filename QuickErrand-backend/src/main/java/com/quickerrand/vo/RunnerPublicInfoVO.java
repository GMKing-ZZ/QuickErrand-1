package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 跑腿员公开信息VO（用户端查看跑腿员信息）
 *
 * @author 周政
 * @date 2026-03-03
 */
@Data
@ApiModel("跑腿员公开信息VO")
public class RunnerPublicInfoVO {

    @ApiModelProperty("跑腿员用户ID")
    private Long userId;

    @ApiModelProperty("跑腿员姓名")
    private String nickname;

    @ApiModelProperty("跑腿员头像")
    private String avatar;

    @ApiModelProperty("信用等级（1-5星）")
    private Integer creditLevel;

    @ApiModelProperty("完成订单数")
    private Integer totalOrders;

    @ApiModelProperty("好评率（百分比）")
    private BigDecimal goodRate;

    @ApiModelProperty("服务时间")
    private String serviceTime;

    @ApiModelProperty("服务范围（米）")
    private Integer serviceRange;

    @ApiModelProperty("认证时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime certTime;
}
