package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单评价列表VO
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
@ApiModel("订单评价列表VO")
public class OrderReviewListVO {

    @ApiModelProperty("评价ID")
    private Long id;

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户昵称")
    private String userNickname;

    @ApiModelProperty("跑腿员ID")
    private Long runnerId;

    @ApiModelProperty("跑腿员姓名")
    private String runnerName;

    @ApiModelProperty("服务质量评分")
    private Integer serviceScore;

    @ApiModelProperty("服务态度评分")
    private Integer attitudeScore;

    @ApiModelProperty("评价内容")
    private String content;

    @ApiModelProperty("评价图片")
    private String images;

    @ApiModelProperty("评价标签")
    private String tags;

    @ApiModelProperty("是否匿名")
    private Integer isAnonymous;

    @ApiModelProperty("状态")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
