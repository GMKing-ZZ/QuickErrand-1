package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quickerrand.entity.Announcement;
import com.quickerrand.mapper.AnnouncementMapper;
import com.quickerrand.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 公告Service实现类
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public Page<Announcement> getAnnouncementList(Integer pageNum, Integer pageSize, String keyword, Integer position, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（标题）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Announcement::getTitle, keyword);
        }

        // 展示位置过滤
        if (position != null) {
            wrapper.eq(Announcement::getPosition, position);
        }

        // 状态过滤
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }

        // 排序：置顶优先，然后按创建时间降序
        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreateTime);

        Page<Announcement> page = new Page<>(pageNum, pageSize);
        return page(page, wrapper);
    }
}
