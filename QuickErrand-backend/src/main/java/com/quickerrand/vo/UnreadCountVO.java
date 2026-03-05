package com.quickerrand.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 未读消息统计VO
 *
 * @author 周政
 * @date 2026-03-03
 */
@Data
@ApiModel("未读消息统计")
public class UnreadCountVO {

    @ApiModelProperty("系统消息未读数")
    private Integer messageUnread;

    @ApiModelProperty("聊天消息未读数")
    private Integer chatUnread;

    @ApiModelProperty("总未读数")
    private Integer totalUnread;
}
