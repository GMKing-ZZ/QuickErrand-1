package com.quickerrand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.entity.OrderType;
import com.quickerrand.service.OrderTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理员订单类型管理控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员订单类型管理接口")
@RestController
@RequestMapping("/admin/orderType")
public class AdminOrderTypeController {

    @Autowired
    private OrderTypeService orderTypeService;

    @ApiOperation("获取订单类型列表")
    @GetMapping("/list")
    public Result<Page<OrderType>> getOrderTypeList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<OrderType> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(OrderType::getTypeName, keyword)
                   .or().like(OrderType::getTypeDesc, keyword);
        }

        // 状态过滤
        if (status != null) {
            wrapper.eq(OrderType::getStatus, status);
        }

        // 按排序字段和创建时间排序
        wrapper.orderByAsc(OrderType::getSortOrder)
               .orderByDesc(OrderType::getCreateTime);

        Page<OrderType> page = new Page<>(pageNum, pageSize);
        orderTypeService.page(page, wrapper);

        return Result.success(page);
    }

    @ApiOperation("获取订单类型详情")
    @GetMapping("/{id}")
    public Result<OrderType> getOrderTypeDetail(@PathVariable Long id) {
        OrderType orderType = orderTypeService.getById(id);
        if (orderType == null) {
            return Result.error("订单类型不存在");
        }
        return Result.success(orderType);
    }

    @ApiOperation("创建订单类型")
    @PostMapping
    public Result<Void> createOrderType(@Valid @RequestBody OrderType orderType) {
        // 设置默认值
        if (orderType.getStatus() == null) {
            orderType.setStatus(1); // 默认启用
        }
        if (orderType.getSortOrder() == null) {
            orderType.setSortOrder(0);
        }

        orderTypeService.save(orderType);
        log.info("创建订单类型成功，类型名称：{}", orderType.getTypeName());
        return Result.success();
    }

    @ApiOperation("更新订单类型")
    @PutMapping("/{id}")
    public Result<Void> updateOrderType(@PathVariable Long id, @Valid @RequestBody OrderType orderType) {
        OrderType existingOrderType = orderTypeService.getById(id);
        if (existingOrderType == null) {
            return Result.error("订单类型不存在");
        }

        orderType.setId(id);
        orderTypeService.updateById(orderType);
        log.info("更新订单类型成功，类型ID：{}，类型名称：{}", id, orderType.getTypeName());
        return Result.success();
    }

    @ApiOperation("删除订单类型")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrderType(@PathVariable Long id) {
        OrderType orderType = orderTypeService.getById(id);
        if (orderType == null) {
            return Result.error("订单类型不存在");
        }

        orderTypeService.removeById(id);
        log.info("删除订单类型成功，类型ID：{}，类型名称：{}", id, orderType.getTypeName());
        return Result.success();
    }

    @ApiOperation("更新订单类型状态")
    @PostMapping("/status/{id}")
    public Result<Void> updateOrderTypeStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (status != 0 && status != 1) {
            return Result.error("状态参数错误");
        }

        OrderType orderType = orderTypeService.getById(id);
        if (orderType == null) {
            return Result.error("订单类型不存在");
        }

        orderType.setStatus(status);
        orderTypeService.updateById(orderType);
        log.info("更新订单类型状态成功，类型ID：{}，新状态：{}", id, status);
        return Result.success();
    }
}
