package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quickerrand.entity.Banner;
import com.quickerrand.mapper.BannerMapper;
import com.quickerrand.service.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 轮播图Service实现类
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public Page<Banner> getBannerList(Integer pageNum, Integer pageSize, String keyword, Integer position, Integer status) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（标题）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Banner::getTitle, keyword);
        }

        // 展示位置过滤
        if (position != null) {
            wrapper.eq(Banner::getPosition, position);
        }

        // 状态过滤
        if (status != null) {
            wrapper.eq(Banner::getStatus, status);
        }

        // 排序：按排序字段升序，然后按创建时间降序
        wrapper.orderByAsc(Banner::getSortOrder)
                .orderByDesc(Banner::getCreateTime);

        Page<Banner> page = new Page<>(pageNum, pageSize);
        return page(page, wrapper);
    }
}
