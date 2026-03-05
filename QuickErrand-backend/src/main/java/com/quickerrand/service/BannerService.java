package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.quickerrand.entity.Banner;

/**
 * 轮播图Service接口
 *
 * @author 周政
 * @date 2026-01-27
 */
public interface BannerService extends IService<Banner> {

    /**
     * 获取轮播图列表（分页）
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 关键词
     * @param position 展示位置（1首页2个人中心）
     * @param status 状态（0禁用1启用）
     * @return 轮播图分页列表
     */
    Page<Banner> getBannerList(Integer pageNum, Integer pageSize, String keyword, Integer position, Integer status);
}
