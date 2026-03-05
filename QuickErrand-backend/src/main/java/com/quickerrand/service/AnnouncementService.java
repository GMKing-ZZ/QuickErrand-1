package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.quickerrand.entity.Announcement;

/**
 * 公告Service接口
 *
 * @author 周政
 * @date 2026-01-27
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 获取公告列表（分页）
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 关键词
     * @param position 展示位置（1首页2个人中心）
     * @param status 状态（0草稿1已发布）
     * @return 公告分页列表
     */
    Page<Announcement> getAnnouncementList(Integer pageNum, Integer pageSize, String keyword, Integer position, Integer status);
}
