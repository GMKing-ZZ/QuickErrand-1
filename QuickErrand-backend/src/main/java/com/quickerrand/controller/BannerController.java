package com.quickerrand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quickerrand.common.Result;
import com.quickerrand.entity.Banner;
import com.quickerrand.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端轮播图控制器
 *
 * @author 周政
 * @date 2026-02-01
 */
@Slf4j
@Api(tags = "轮播图接口")
@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation("获取轮播图列表")
    @GetMapping("/list")
    public Result<List<Banner>> getBannerList(
            @RequestParam(required = false) Integer position) {
        // 只返回启用的轮播图，按排序字段和创建时间排序
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1); // 只查询启用的
        if (position != null) {
            wrapper.eq(Banner::getPosition, position); // 如果指定了位置，则过滤
        }
        wrapper.orderByAsc(Banner::getSortOrder) // 按排序字段升序
                .orderByDesc(Banner::getCreateTime); // 按创建时间降序
        
        List<Banner> banners = bannerService.list(wrapper);
        return Result.success(banners);
    }
}
