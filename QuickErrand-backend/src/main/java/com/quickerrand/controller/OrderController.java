package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.dto.CalculateFeeDTO;
import com.quickerrand.dto.CreateOrderDTO;
import com.quickerrand.dto.EvaluateOrderDTO;
import com.quickerrand.service.OrderService;
import com.quickerrand.service.OrderTypeService;
import com.quickerrand.service.RunnerInfoService;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.OrderTypeVO;
import com.quickerrand.vo.OrderVO;
import com.quickerrand.vo.RunnerPublicInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单控制器
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Api(tags = "订单接口")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderTypeService orderTypeService;

    @Autowired
    private RunnerInfoService runnerInfoService;

    @ApiOperation("获取订单类型列表")
    @GetMapping("/types")
    public Result<List<OrderTypeVO>> getOrderTypes() {
        List<OrderTypeVO> orderTypes = orderTypeService.getOrderTypeList();
        return Result.success(orderTypes);
    }

    @ApiOperation("计算订单费用")
    @PostMapping("/calculateFee")
    public Result<BigDecimal> calculateFee(@Validated @RequestBody CalculateFeeDTO calculateFeeDTO) {
        BigDecimal fee = orderService.calculateFee(calculateFeeDTO);
        return Result.success(fee);
    }

    @ApiOperation("创建订单")
    @PostMapping("/create")
    public Result<Long> createOrder(@Validated @RequestBody CreateOrderDTO createOrderDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        Long orderId = orderService.createOrder(userId, createOrderDTO);
        return Result.success(orderId);
    }

    @ApiOperation("支付订单")
    @PostMapping("/pay/{orderId}")
    public Result<Object> payOrder(@PathVariable Long orderId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Object payResult = orderService.payOrder(userId, orderId);
        return Result.success(payResult);
    }

    @ApiOperation("微信支付回调")
    @PostMapping("/wxPayNotify")
    public String wxPayNotify(@RequestBody String notifyData) {
        try {
            orderService.handleWxPayNotify(notifyData);
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>";
        }
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        Long userId = SecurityUtils.getCurrentUserId();
        OrderVO orderVO = orderService.getOrderDetail(userId, orderId);
        return Result.success(orderVO);
    }

    @ApiOperation("获取订单列表")
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(@RequestParam(required = false) Integer status) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<OrderVO> orderList = orderService.getOrderList(userId, status);
        return Result.success(orderList);
    }

    @ApiOperation("取消订单")
    @PostMapping("/cancel")
    public Result<Void> cancelOrder(@RequestBody CancelOrderRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.cancelOrder(userId, request.getOrderId(), request.getReason());
        return Result.success();
    }

    @ApiOperation("搜索订单")
    @GetMapping("/search")
    public Result<List<OrderVO>> searchOrders(@RequestParam String keyword) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<OrderVO> orderList = orderService.searchOrders(userId, keyword);
        return Result.success(orderList);
    }

    @ApiOperation("评价订单")
    @PostMapping("/evaluate")
    public Result<Void> evaluateOrder(@Validated @RequestBody EvaluateOrderDTO evaluateOrderDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.evaluateOrder(userId, evaluateOrderDTO);
        return Result.success();
    }

    @ApiOperation("获取跑腿员公开信息")
    @GetMapping("/runner/{runnerUserId}")
    public Result<RunnerPublicInfoVO> getRunnerPublicInfo(@PathVariable Long runnerUserId) {
        RunnerPublicInfoVO runnerPublicInfo = runnerInfoService.getRunnerPublicInfo(runnerUserId);
        return Result.success(runnerPublicInfo);
    }

    /**
     * 取消订单请求
     */
    @lombok.Data
    public static class CancelOrderRequest {
        private Long orderId;
        private String reason;
    }

}
