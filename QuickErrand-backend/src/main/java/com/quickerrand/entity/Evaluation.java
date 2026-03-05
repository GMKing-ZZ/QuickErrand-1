package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单评价实体类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_evaluation")
public class Evaluation extends BaseEntity {

    /**
     * 禁用逻辑删除（t_evaluation 表没有 deleted 字段）
     */
    @TableField(exist = false)
    private Integer deleted;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 跑腿员ID
     */
    private Long runnerId;

    /**
     * 服务质量评分（1-5）
     */
    private Integer serviceScore;

    /**
     * 服务态度评分（1-5）
     */
    private Integer attitudeScore;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片（JSON数组）
     */
    private String images;

    /**
     * 评价标签（JSON数组）
     */
    private String tags;

    /**
     * 状态（0正常1已删除）
     */
    private Integer status;

}
