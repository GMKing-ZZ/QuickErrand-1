package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quickerrand.entity.EarningsRecord;
import com.quickerrand.entity.Order;
import com.quickerrand.mapper.EarningsRecordMapper;
import com.quickerrand.mapper.OrderMapper;
import com.quickerrand.service.EarningsService;
import com.quickerrand.vo.EarningsDetailVO;
import com.quickerrand.vo.EarningsStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收益服务实现
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Service
public class EarningsServiceImpl implements EarningsService {

    @Autowired
    private EarningsRecordMapper earningsRecordMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public EarningsStatisticsVO getEarningsStatistics(Long userId) {
        EarningsStatisticsVO statistics = new EarningsStatisticsVO();

        // 1. 查询该跑腿员的所有收益记录（主要用于提现、奖励等信息）
        List<EarningsRecord> allRecords = earningsRecordMapper.selectList(
                new LambdaQueryWrapper<EarningsRecord>()
                        .eq(EarningsRecord::getUserId, userId)
        );

        // 2. 实时统计：直接根据已完成订单计算订单收益，保证和订单列表实时同步
        LambdaQueryWrapper<Order> completedOrderWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getRunnerId, userId)
                .eq(Order::getStatus, 4); // 已完成
        List<Order> completedOrders = orderMapper.selectList(completedOrderWrapper);

        // 所有已完成订单的跑腿员收益
        BigDecimal totalOrderEarnings = completedOrders.stream()
                .map(o -> o.getRunnerFee() == null ? BigDecimal.ZERO : o.getRunnerFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 今日、本月时间范围
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        // 今日收益：按订单完成时间统计
        BigDecimal todayEarnings = completedOrders.stream()
                .filter(o -> o.getCompleteTime() != null
                        && !o.getCompleteTime().isBefore(todayStart)
                        && !o.getCompleteTime().isAfter(todayEnd))
                .map(o -> o.getRunnerFee() == null ? BigDecimal.ZERO : o.getRunnerFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 本月收益：按订单完成时间统计
        BigDecimal monthEarnings = completedOrders.stream()
                .filter(o -> o.getCompleteTime() != null
                        && !o.getCompleteTime().isBefore(monthStart))
                .map(o -> o.getRunnerFee() == null ? BigDecimal.ZERO : o.getRunnerFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 提现金额与奖励等仍然从收益记录表中统计
        BigDecimal withdrawnAmount = allRecords.stream()
                .filter(r -> r.getType() == 3)
                .map(EarningsRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bonusEarnings = allRecords.stream()
                .filter(r -> r.getType() == 2)
                .map(EarningsRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 如果系统已经产生了结算记录，则优先使用结算记录中的待结算/已结算金额；
        // 否则使用简单的实时统计方式：全部视为已结算，避免页面显示为0。
        boolean hasNonWithdrawRecords = allRecords.stream().anyMatch(r -> r.getType() != 3);

        BigDecimal pendingEarnings;
        BigDecimal settledEarnings;

        if (hasNonWithdrawRecords) {
            // 原有按收益记录的统计逻辑
            pendingEarnings = allRecords.stream()
                    .filter(r -> r.getType() != 3 && r.getStatus() == 1)
                    .map(EarningsRecord::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            settledEarnings = allRecords.stream()
                    .filter(r -> r.getType() != 3 && r.getStatus() == 2)
                    .map(EarningsRecord::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            // 当前尚无收益结算记录，全部完成订单收益视为已结算
            pendingEarnings = BigDecimal.ZERO;
            settledEarnings = totalOrderEarnings;
        }

        // 4. 填充统计结果
        statistics.setTotalEarnings(totalOrderEarnings);
        statistics.setPendingEarnings(pendingEarnings);
        statistics.setSettledEarnings(settledEarnings);
        // 可提现金额 = 已结算收益 - 已提现金额
        statistics.setWithdrawableAmount(settledEarnings.subtract(withdrawnAmount));

        statistics.setTodayEarnings(todayEarnings);
        statistics.setMonthEarnings(monthEarnings);

        statistics.setOrderEarnings(totalOrderEarnings);
        statistics.setBonusEarnings(bonusEarnings);

        return statistics;
    }

    @Override
    public List<EarningsDetailVO> getEarningsDetails(Long userId) {
        // 查询收益记录
        List<EarningsRecord> records = earningsRecordMapper.selectList(
                new LambdaQueryWrapper<EarningsRecord>()
                        .eq(EarningsRecord::getUserId, userId)
                        .orderByDesc(EarningsRecord::getCreateTime)
        );

        // 查询关联的订单信息
        List<Long> orderIds = records.stream()
                .filter(r -> r.getOrderId() != null)
                .map(EarningsRecord::getOrderId)
                .collect(Collectors.toList());

        Map<Long, Order> orderMap = orderIds.isEmpty() ? Collections.emptyMap() :
                orderMapper.selectBatchIds(orderIds).stream()
                        .collect(Collectors.toMap(Order::getId, o -> o));

        // 转换为VO
        return records.stream().map(record -> {
            EarningsDetailVO vo = new EarningsDetailVO();
            BeanUtils.copyProperties(record, vo);

            // 设置订单编号
            if (record.getOrderId() != null && orderMap.containsKey(record.getOrderId())) {
                vo.setOrderNo(orderMap.get(record.getOrderId()).getOrderNo());
            }

            // 设置类型文本
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
            }

            // 设置状态文本
            switch (record.getStatus()) {
                case 1:
                    vo.setStatusText("待结算");
                    break;
                case 2:
                    vo.setStatusText("已结算");
                    break;
            }

            return vo;
        }).collect(Collectors.toList());
    }
}
