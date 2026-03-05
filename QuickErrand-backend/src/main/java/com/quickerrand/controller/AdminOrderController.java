package com.quickerrand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.dto.OrderQueryDTO;
import com.quickerrand.service.OrderService;
import com.quickerrand.vo.OrderListVO;
import com.quickerrand.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员订单管理控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员订单管理接口")
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("获取订单列表")
    @GetMapping("/list")
    public Result<Page<OrderListVO>> getOrderList(OrderQueryDTO queryDTO) {
        Page<OrderListVO> page = orderService.getAdminOrderList(queryDTO);
        return Result.success(page);
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        OrderVO orderVO = orderService.getAdminOrderDetail(orderId);
        return Result.success(orderVO);
    }

    @ApiOperation("更新订单状态")
    @PostMapping("/status/{orderId}")
    public Result<Void> updateOrderStatus(@PathVariable Long orderId, @RequestParam Integer status) {
        if (status < 0 || status > 5) {
            return Result.error("状态参数错误");
        }
        orderService.updateOrderStatus(orderId, status);
        return Result.success();
    }

    @ApiOperation("删除单个订单")
    @DeleteMapping("/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return Result.success();
    }

    @ApiOperation("批量删除订单")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteOrders(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("订单ID列表不能为空");
        }
        orderService.batchDeleteOrders(ids);
        return Result.success();
    }
}
