package com.quickerrand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.entity.EarningsRecord;
import com.quickerrand.entity.Order;
import com.quickerrand.entity.User;
import com.quickerrand.mapper.EarningsRecordMapper;
import com.quickerrand.mapper.OrderMapper;
import com.quickerrand.service.UserService;
import com.quickerrand.vo.EarningsAdminVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员收益结算控制器
 *
 * 为管理员提供手动结算入口，用于在必要时提前或补充结算跑腿员收益。
 *
 * @author 周政（扩展：收益结算）
 * @date 2026-02-13
 */
@Slf4j
@Api(tags = "管理员收益结算接口")
@RestController
@RequestMapping("/admin/earnings")
public class AdminEarningsController {

    @Autowired
    private EarningsRecordMapper earningsRecordMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderMapper orderMapper;

    @ApiOperation("获取收益结算记录列表")
    @GetMapping("/list")
    public Result<Page<EarningsAdminVO>> getEarningsList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {

        Page<EarningsRecord> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<EarningsRecord> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(EarningsRecord::getType, type);
        }
        if (status != null) {
            wrapper.eq(EarningsRecord::getStatus, status);
        }

        // 关键字：支持按用户ID、订单号（模糊）、备注模糊匹配
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> {
                // 备注模糊
                w.like(EarningsRecord::getRemark, kw);

                // 如果是数字，按用户ID等值匹配
                try {
                    Long userId = Long.parseLong(kw);
                    w.or().eq(EarningsRecord::getUserId, userId);
                } catch (NumberFormatException ignored) {
                }

                // 尝试按订单号模糊查询订单，再按订单ID过滤
                LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
                orderWrapper.like(Order::getOrderNo, kw);
                List<Order> orders = orderMapper.selectList(orderWrapper);
                if (!orders.isEmpty()) {
                    List<Long> ids = orders.stream().map(Order::getId).collect(Collectors.toList());
                    w.or().in(EarningsRecord::getOrderId, ids);
                }
            });
        }

        wrapper.orderByDesc(EarningsRecord::getCreateTime);

        earningsRecordMapper.selectPage(page, wrapper);

        // 预加载用户与订单信息
        List<EarningsRecord> records = page.getRecords();
        List<Long> userIds = records.stream()
                .map(EarningsRecord::getUserId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> orderIds = records.stream()
                .map(EarningsRecord::getOrderId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userMap = userIds.isEmpty()
                ? Collections.emptyMap()
                : userService.listByIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));

        Map<Long, Order> orderMap = orderIds.isEmpty()
                ? Collections.emptyMap()
                : orderMapper.selectBatchIds(orderIds).stream()
                    .collect(Collectors.toMap(Order::getId, o -> o));

        // 转为 VO
        Page<EarningsAdminVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<EarningsAdminVO> voList = records.stream().map(record -> {
            EarningsAdminVO vo = new EarningsAdminVO();
            BeanUtils.copyProperties(record, vo);

            // 用户昵称
            User user = userMap.get(record.getUserId());
            if (user != null) {
                vo.setUserNickname(user.getNickname());
            }

            // 订单号
            if (record.getOrderId() != null) {
                Order order = orderMap.get(record.getOrderId());
                if (order != null) {
                    vo.setOrderNo(order.getOrderNo());
                }
            }

            // 类型文本
            if (record.getType() != null) {
                switch (record.getType()) {
                    case 1:
                        vo.setTypeText("订单收益");
                        break;
                    case 2:
                        vo.setTypeText("奖励");
                        break;
                    case 3:
                        vo.setTypeText("提现");
                        break;
                    default:
                        vo.setTypeText("未知类型");
                }
            }

            // 状态文本
            if (record.getStatus() != null) {
                switch (record.getStatus()) {
                    case 1:
                        vo.setStatusText("待结算");
                        break;
                    case 2:
                        vo.setStatusText("已结算");
                        break;
                    default:
                        vo.setStatusText("未知状态");
                }
            }

            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return Result.success(voPage);
    }

    @ApiOperation("手动结算指定收益记录")
    @PostMapping("/settle")
    public Result<Integer> manualSettle(@RequestBody SettleRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return Result.success(0);
        }

        LambdaUpdateWrapper<EarningsRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(EarningsRecord::getId, request.getIds())
               .ne(EarningsRecord::getType, 3)   // 排除提现记录
               .eq(EarningsRecord::getStatus, 1) // 仅结算待结算记录
               .set(EarningsRecord::getStatus, 2);

        int updated = earningsRecordMapper.update(null, wrapper);
        log.info("管理员手动结算收益记录，ids={}，本次更新数量={}", request.getIds(), updated);
        return Result.success(updated);
    }

    @ApiOperation("删除收益记录")
    @PostMapping("/delete")
    public Result<Integer> deleteEarnings(@RequestBody DeleteRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return Result.success(0);
        }

        int deleted = earningsRecordMapper.deleteBatchIds(request.getIds());
        log.info("管理员删除收益记录，ids={}，删除数量={}", request.getIds(), deleted);
        return Result.success(deleted);
    }

    @ApiOperation("删除单条收益记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEarningsById(@PathVariable Long id) {
        earningsRecordMapper.deleteById(id);
        log.info("管理员删除单条收益记录，id={}", id);
        return Result.success();
    }

    @Data
    public static class SettleRequest {
        @NotEmpty(message = "待结算记录ID不能为空")
        private List<Long> ids;
    }

    @Data
    public static class DeleteRequest {
        @NotEmpty(message = "待删除记录ID不能为空")
        private List<Long> ids;
    }
}

