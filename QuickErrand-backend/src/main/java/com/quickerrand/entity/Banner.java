package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 轮播图实体类
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_banner")
public class Banner extends BaseEntity {

    /**
     * 禁用逻辑删除（t_banner 表没有 deleted 字段）
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer deleted;

    /**
     * 轮播图标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 跳转链接（可选）
     */
    private String linkUrl;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 展示位置：1-首页 2-个人中心
     */
    private Integer position;

}
