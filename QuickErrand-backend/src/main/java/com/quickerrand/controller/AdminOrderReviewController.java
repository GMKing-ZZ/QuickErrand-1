package com.quickerrand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.entity.Evaluation;
import com.quickerrand.service.OrderReviewService;
import com.quickerrand.vo.OrderReviewListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员评价管理控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员评价管理接口")
@RestController
@RequestMapping("/admin/review")
public class AdminOrderReviewController {

    @Autowired
    private OrderReviewService orderReviewService;

    @ApiOperation("获取评价列表")
    @GetMapping("/list")
    public Result<Page<OrderReviewListVO>> getReviewList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer status) {

        Page<OrderReviewListVO> page = orderReviewService.getReviewList(pageNum, pageSize, keyword, rating, status);
        return Result.success(page);
    }

    @ApiOperation("获取评价详情")
    @GetMapping("/{id}")
    public Result<Evaluation> getReviewById(@PathVariable Long id) {
        Evaluation review = orderReviewService.getById(id);
        return Result.success(review);
    }

    @ApiOperation("更新评价状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateReviewStatus(@PathVariable Long id, @RequestParam Integer status) {
        Evaluation review = new Evaluation();
        review.setId(id);
        review.setStatus(status);
        orderReviewService.updateById(review);
        log.info("更新评价状态成功，id={}，status={}", id, status);
        return Result.success();
    }

    @ApiOperation("删除评价")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        orderReviewService.removeById(id);
        log.info("删除评价成功，id={}", id);
        return Result.success();
    }

    @ApiOperation("批量删除评价")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteReviews(@RequestBody Map<String, List<Long>> requestBody) {
        List<Long> ids = requestBody.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的评价");
        }
        orderReviewService.removeByIds(ids);
        log.info("批量删除评价成功，ids={}", ids);
        return Result.success();
    }
}
