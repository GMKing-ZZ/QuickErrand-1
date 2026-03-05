package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.service.OrderService;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 跑腿员订单控制器
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Api(tags = "跑腿员订单接口")
@RestController
@RequestMapping("/runner/order")
public class RunnerOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("获取待接单列表")
    @GetMapping("/pending")
    public Result<List<OrderVO>> getPendingOrders() {
        List<OrderVO> orders = orderService.getPendingOrders();
        return Result.success(orders);
    }

    @ApiOperation("接单")
    @PostMapping("/accept/{orderId}")
    public Result<Void> acceptOrder(@PathVariable Long orderId) {
        Long runnerId = SecurityUtils.getCurrentUserId();
        orderService.acceptOrder(runnerId, orderId);
        return Result.success();
    }

    @ApiOperation("获取进行中订单列表")
    @GetMapping("/active")
    public Result<List<OrderVO>> getActiveOrders() {
        Long runnerId = SecurityUtils.getCurrentUserId();
        List<OrderVO> orders = orderService.getRunnerActiveOrders(runnerId);
        return Result.success(orders);
    }

    @ApiOperation("获取已完成订单列表")
    @GetMapping("/completed")
    public Result<List<OrderVO>> getCompletedOrders() {
        Long runnerId = SecurityUtils.getCurrentUserId();
        List<OrderVO> orders = orderService.getRunnerCompletedOrders(runnerId);
        return Result.success(orders);
    }

    @ApiOperation("取件（更新订单状态为配送中）")
    @PostMapping("/pickup/{orderId}")
    public Result<Void> pickupOrder(@PathVariable Long orderId) {
        Long runnerId = SecurityUtils.getCurrentUserId();
        orderService.pickupOrder(runnerId, orderId);
        return Result.success();
    }

    @ApiOperation("完成订单（验证收货码）")
    @PostMapping("/complete/{orderId}")
    public Result<Void> completeOrder(@PathVariable Long orderId, @RequestBody CompleteOrderRequest request) {
        Long runnerId = SecurityUtils.getCurrentUserId();
        orderService.completeOrder(runnerId, orderId, request.getPickupCode());
        return Result.success();
    }

    /**
     * 完成订单请求
     */
    @lombok.Data
    public static class CompleteOrderRequest {
        private String pickupCode;
    }

    @ApiOperation("获取骑手评价列表")
    @GetMapping("/reviews")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.quickerrand.vo.OrderReviewListVO>> getRunnerReviews(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long runnerId = SecurityUtils.getCurrentUserId();
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.quickerrand.vo.OrderReviewListVO> page = 
                orderService.getRunnerReviews(runnerId, pageNum, pageSize);
        return Result.success(page);
    }

}
