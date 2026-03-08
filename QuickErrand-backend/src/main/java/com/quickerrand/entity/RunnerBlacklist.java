package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_runner_blacklist")
public class RunnerBlacklist extends BaseEntity {

    private Long userId;

    private Long runnerId;

    private String reason;
}
