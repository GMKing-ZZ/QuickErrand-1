package com.quickerrand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.entity.Banner;
import com.quickerrand.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理员轮播图管理控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员轮播图管理接口")
@RestController
@RequestMapping("/admin/banner")
public class AdminBannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation("获取轮播图列表")
    @GetMapping("/list")
    public Result<Page<Banner>> getBannerList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer position,
            @RequestParam(required = false) Integer status) {

        Page<Banner> page = bannerService.getBannerList(pageNum, pageSize, keyword, position, status);
        return Result.success(page);
    }

    @ApiOperation("获取轮播图详情")
    @GetMapping("/{id}")
    public Result<Banner> getBannerById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        return Result.success(banner);
    }

    @ApiOperation("创建轮播图")
    @PostMapping
    public Result<Void> createBanner(@Validated @RequestBody Banner banner) {
        banner.setCreateTime(LocalDateTime.now());
        banner.setUpdateTime(LocalDateTime.now());

        // 设置默认值
        if (banner.getStatus() == null) {
            banner.setStatus(1); // 默认启用
        }
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0); // 默认排序为0
        }

        bannerService.save(banner);
        log.info("创建轮播图成功，id={}", banner.getId());
        return Result.success();
    }

    @ApiOperation("更新轮播图")
    @PutMapping("/{id}")
    public Result<Void> updateBanner(@PathVariable Long id, @Validated @RequestBody Banner banner) {
        banner.setId(id);
        banner.setUpdateTime(LocalDateTime.now());
        bannerService.updateById(banner);
        log.info("更新轮播图成功，id={}", id);
        return Result.success();
    }

    @ApiOperation("删除轮播图")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        bannerService.removeById(id);
        log.info("删除轮播图成功，id={}", id);
        return Result.success();
    }

    @ApiOperation("更新轮播图状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateBannerStatus(@PathVariable Long id, @RequestParam Integer status) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus(status);
        banner.setUpdateTime(LocalDateTime.now());
        bannerService.updateById(banner);
        log.info("更新轮播图状态成功，id={}，status={}", id, status);
        return Result.success();
    }

    @ApiOperation("批量删除轮播图")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteBanners(@RequestBody java.util.List<Long> ids) {
        bannerService.removeByIds(ids);
        log.info("批量删除轮播图成功，ids={}", ids);
        return Result.success();
    }
}
