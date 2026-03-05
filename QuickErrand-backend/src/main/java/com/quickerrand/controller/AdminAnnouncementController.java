package com.quickerrand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.entity.Announcement;
import com.quickerrand.entity.User;
import com.quickerrand.mapper.UserMapper;
import com.quickerrand.service.AnnouncementService;
import com.quickerrand.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员公告管理控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员公告管理接口")
@RestController
@RequestMapping("/admin/announcement")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserMapper userMapper;

    @ApiOperation("获取公告列表")
    @GetMapping("/list")
    public Result<Page<Announcement>> getAnnouncementList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer position,
            @RequestParam(required = false) Integer status) {

        Page<Announcement> page = announcementService.getAnnouncementList(pageNum, pageSize, keyword, position, status);
        return Result.success(page);
    }

    @ApiOperation("获取公告详情")
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        return Result.success(announcement);
    }

    @ApiOperation("创建公告")
    @PostMapping
    public Result<Void> createAnnouncement(@Validated @RequestBody Announcement announcement) {
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());

        if (announcement.getStatus() == null) {
            announcement.setStatus(1);
        }
        if (announcement.getIsTop() == null) {
            announcement.setIsTop(0);
        }
        if (announcement.getPublishTime() == null && announcement.getStatus() == 1) {
            announcement.setPublishTime(LocalDateTime.now());
        }

        announcementService.save(announcement);
        log.info("创建公告成功，id={}", announcement.getId());

        if (announcement.getStatus() == 1) {
            pushAnnouncementNotification(announcement);
        }

        return Result.success();
    }

    @ApiOperation("更新公告")
    @PutMapping("/{id}")
    public Result<Void> updateAnnouncement(@PathVariable Long id, @Validated @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcement.setUpdateTime(LocalDateTime.now());
        announcementService.updateById(announcement);
        log.info("更新公告成功，id={}", id);
        return Result.success();
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.removeById(id);
        log.info("删除公告成功，id={}", id);
        return Result.success();
    }

    @ApiOperation("更新公告状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateAnnouncementStatus(@PathVariable Long id, @RequestParam Integer status) {
        Announcement existingAnnouncement = announcementService.getById(id);
        if (existingAnnouncement == null) {
            return Result.error("公告不存在");
        }

        Integer oldStatus = existingAnnouncement.getStatus();

        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setStatus(status);
        announcement.setUpdateTime(LocalDateTime.now());
        if (status == 1 && existingAnnouncement.getPublishTime() == null) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        announcementService.updateById(announcement);
        log.info("更新公告状态成功，id={}，status={}", id, status);

        if (oldStatus != 1 && status == 1) {
            existingAnnouncement.setStatus(status);
            pushAnnouncementNotification(existingAnnouncement);
        }

        return Result.success();
    }

    @ApiOperation("更新公告置顶状态")
    @PutMapping("/{id}/top")
    public Result<Void> updateAnnouncementTop(@PathVariable Long id, @RequestParam Integer isTop) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setIsTop(isTop);
        announcement.setUpdateTime(LocalDateTime.now());
        announcementService.updateById(announcement);
        log.info("更新公告置顶状态成功，id={}，isTop={}", id, isTop);
        return Result.success();
    }

    private void pushAnnouncementNotification(Announcement announcement) {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .eq(User::getStatus, 1)
                        .eq(User::getDeleted, 0)
        );

        if (users.isEmpty()) {
            log.warn("没有可推送公告的用户");
            return;
        }

        List<Long> userIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        String title = "新公告：" + announcement.getTitle();
        String content = announcement.getTitle();

        messageService.pushMessageToUsers(userIds, title, content, 2, announcement.getId());

        log.info("公告已推送给 {} 位用户，公告ID：{}", userIds.size(), announcement.getId());
    }
}
