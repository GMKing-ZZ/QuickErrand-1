package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公告实体类
 *
 * @author 周政
 * @date 2026-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_announcement")
public class Announcement extends BaseEntity {

    /**
     * 禁用逻辑删除（t_announcement 表没有 deleted 字段）
     */
    @TableField(exist = false)
    private Integer deleted;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容（富文本）
     */
    private String content;

    /**
     * 展示位置：1-首页 2-个人中心
     */
    private Integer position;

    /**
     * 是否置顶：0-否 1-是
     */
    private Integer isTop;

    /**
     * 状态：0-草稿 1-已发布
     */
    private Integer status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 阅读量
     */
    private Integer readCount;

}
