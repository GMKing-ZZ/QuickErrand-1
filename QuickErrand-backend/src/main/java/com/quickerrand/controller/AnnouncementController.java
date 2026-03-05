package com.quickerrand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quickerrand.common.Result;
import com.quickerrand.entity.Announcement;
import com.quickerrand.service.AnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端公告接口
 *
 * 用于在用户首页、跑腿员首页等位置展示已发布公告。
 *
 * @author 周政
 * @date 2026-02-03
 */
@Slf4j
@Api(tags = "公告接口")
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @ApiOperation("获取已发布的公告列表（按置顶和时间排序）")
    @GetMapping("/list")
    public Result<List<Announcement>> getAnnouncements(
            @RequestParam(required = false) Integer position,
            @RequestParam(required = false, defaultValue = "3") Integer limit) {

        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        // 只返回已发布的公告
        wrapper.eq(Announcement::getStatus, 1);
        // 过滤展示位置：1-首页 2-个人中心（用户端 / 跑腿员端可共用）
        if (position != null) {
            wrapper.eq(Announcement::getPosition, position);
        }
        // 置顶优先，其次按发布时间/创建时间倒序
        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getPublishTime)
                .orderByDesc(Announcement::getCreateTime);

        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }

        List<Announcement> list = announcementService.list(wrapper);
        return Result.success(list);
    }
}

