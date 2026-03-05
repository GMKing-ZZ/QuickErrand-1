package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quickerrand.entity.Order;
import com.quickerrand.entity.OrderType;
import com.quickerrand.entity.RunnerInfo;
import com.quickerrand.entity.User;
import com.quickerrand.service.*;
import com.quickerrand.vo.DashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计Service实现类
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RunnerInfoService runnerInfoService;

    @Autowired
    private OrderTypeService orderTypeService;

    @Override
    public DashboardVO getDashboardStatistics() {
        DashboardVO vo = new DashboardVO();

        // 获取总体统计数据
        vo.setTotalUsers(userService.count());
        vo.setTotalOrders(orderService.count());

        // 统计认证跑腿员数量（certStatus = 2）
        LambdaQueryWrapper<RunnerInfo> runnerWrapper = new LambdaQueryWrapper<>();
        runnerWrapper.eq(RunnerInfo::getCertStatus, 2);
        vo.setTotalRunners(runnerInfoService.count(runnerWrapper));

        // 统计总收入：排除「待支付(1)」和「已取消(5)」状态的订单，其他状态都计入收入
        LambdaQueryWrapper<Order> revenueWrapper = new LambdaQueryWrapper<>();
        revenueWrapper.ne(Order::getStatus, 1)  // 非待支付
                .ne(Order::getStatus, 5);      // 非已取消
        List<Order> revenueOrders = orderService.list(revenueWrapper);
        BigDecimal totalRevenue = revenueOrders.stream()
                .map(Order::getServiceFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalRevenue(totalRevenue);

        // 统计总平台服务费：同样排除「待支付」和「已取消」状态的订单
        BigDecimal totalPlatformFee = revenueOrders.stream()
                .map(Order::getPlatformFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalPlatformFee(totalPlatformFee);

        // 获取今日统计数据
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 今日新增用户
        LambdaQueryWrapper<User> todayUserWrapper = new LambdaQueryWrapper<>();
        todayUserWrapper.between(User::getCreateTime, todayStart, todayEnd);
        vo.setTodayNewUsers(userService.count(todayUserWrapper));

        // 今日新增订单
        LambdaQueryWrapper<Order> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.between(Order::getCreateTime, todayStart, todayEnd);
        vo.setTodayNewOrders(orderService.count(todayOrderWrapper));

        // 今日新增跑腿员
        LambdaQueryWrapper<RunnerInfo> todayRunnerWrapper = new LambdaQueryWrapper<>();
        todayRunnerWrapper.eq(RunnerInfo::getCertStatus, 2);
        todayRunnerWrapper.between(RunnerInfo::getCreateTime, todayStart, todayEnd);
        vo.setTodayNewRunners(runnerInfoService.count(todayRunnerWrapper));

        // 今日收入：同样排除「待支付」与「已取消」，按完成时间统计
        LambdaQueryWrapper<Order> todayRevenueWrapper = new LambdaQueryWrapper<>();
        todayRevenueWrapper.ne(Order::getStatus, 1)
                .ne(Order::getStatus, 5);
        todayRevenueWrapper.between(Order::getCompleteTime, todayStart, todayEnd);
        List<Order> todayRevenueOrders = orderService.list(todayRevenueWrapper);
        BigDecimal todayRevenue = todayRevenueOrders.stream()
                .map(Order::getServiceFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTodayRevenue(todayRevenue);

        // 计算周同比和日同比
        calculateRatios(vo, todayStart, todayEnd);

        // 订单状态统计
        vo.setOrderStatusStatistics(getOrderStatusStatistics());

        // 最近7天订单趋势
        vo.setOrderTrend(getOrderTrend());

        // 最近7天收入趋势
        vo.setRevenueTrend(getRevenueTrend());

        // 订单类型统计
        vo.setOrderTypeStatistics(getOrderTypeStatistics());

        log.info("获取数据看板统计数据成功");
        return vo;
    }

    /**
     * 获取订单状态统计
     */
    private List<DashboardVO.OrderStatusStatistics> getOrderStatusStatistics() {
        List<DashboardVO.OrderStatusStatistics> list = new ArrayList<>();

        // 定义订单状态映射（数据库状态值：1待支付2待接单3服务中4已完成5已取消）
        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(1, "待支付");
        statusMap.put(2, "待接单");
        statusMap.put(3, "服务中");
        statusMap.put(4, "已完成");
        statusMap.put(5, "已取消");

        // 统计每个状态的订单数量
        for (Map.Entry<Integer, String> entry : statusMap.entrySet()) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getStatus, entry.getKey());
            long count = orderService.count(wrapper);

            DashboardVO.OrderStatusStatistics statistics = new DashboardVO.OrderStatusStatistics();
            statistics.setStatusName(entry.getValue());
            statistics.setCount(count);
            list.add(statistics);
        }

        return list;
    }

    /**
     * 获取最近7天订单趋势
     */
    private List<DashboardVO.DailyStatistics> getOrderTrend() {
        List<DashboardVO.DailyStatistics> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(Order::getCreateTime, dayStart, dayEnd);
            long count = orderService.count(wrapper);

            DashboardVO.DailyStatistics statistics = new DashboardVO.DailyStatistics();
            statistics.setDate(date.format(formatter));
            statistics.setValue(new BigDecimal(count));
            list.add(statistics);
        }

        return list;
    }

    /**
     * 获取最近7天收入趋势
     */
    private List<DashboardVO.DailyStatistics> getRevenueTrend() {
        List<DashboardVO.DailyStatistics> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            // 收入统计同样排除「待支付」与「已取消」
            wrapper.ne(Order::getStatus, 1)
                    .ne(Order::getStatus, 5);
            wrapper.between(Order::getCompleteTime, dayStart, dayEnd);
            List<Order> orders = orderService.list(wrapper);

            BigDecimal revenue = orders.stream()
                    .map(Order::getServiceFee)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            DashboardVO.DailyStatistics statistics = new DashboardVO.DailyStatistics();
            statistics.setDate(date.format(formatter));
            statistics.setValue(revenue);
            list.add(statistics);
        }

        return list;
    }

    /**
     * 获取订单类型统计
     */
    private List<DashboardVO.OrderTypeStatistics> getOrderTypeStatistics() {
        List<DashboardVO.OrderTypeStatistics> list = new ArrayList<>();

        // 获取所有订单类型
        List<OrderType> orderTypes = orderTypeService.list();

        for (OrderType orderType : orderTypes) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getOrderTypeId, orderType.getId());
            long count = orderService.count(wrapper);

            DashboardVO.OrderTypeStatistics statistics = new DashboardVO.OrderTypeStatistics();
            statistics.setTypeName(orderType.getTypeName());
            statistics.setCount(count);
            list.add(statistics);
        }

        return list;
    }

    /**
     * 计算周同比和日同比
     */
    private void calculateRatios(DashboardVO vo, LocalDateTime todayStart, LocalDateTime todayEnd) {
        // 计算收入同比
        // 上周同期（7天前）
        LocalDateTime lastWeekStart = todayStart.minusDays(7);
        LocalDateTime lastWeekEnd = todayEnd.minusDays(7);
        BigDecimal lastWeekRevenue = getRevenueBetween(lastWeekStart, lastWeekEnd);
        BigDecimal todayRevenue = vo.getTodayRevenue() != null ? vo.getTodayRevenue() : BigDecimal.ZERO;
        vo.setRevenueWeekRatio(calculateRatio(todayRevenue, lastWeekRevenue));

        // 昨日同期
        LocalDateTime yesterdayStart = todayStart.minusDays(1);
        LocalDateTime yesterdayEnd = todayEnd.minusDays(1);
        BigDecimal yesterdayRevenue = getRevenueBetween(yesterdayStart, yesterdayEnd);
        vo.setRevenueDayRatio(calculateRatio(todayRevenue, yesterdayRevenue));

        // 计算用户数同比
        // 上周同期新增用户数
        LambdaQueryWrapper<User> lastWeekUserWrapper = new LambdaQueryWrapper<>();
        lastWeekUserWrapper.between(User::getCreateTime, lastWeekStart, lastWeekEnd);
        Long lastWeekUsers = userService.count(lastWeekUserWrapper);
        Long todayUsers = vo.getTodayNewUsers() != null ? vo.getTodayNewUsers() : 0L;
        vo.setUsersWeekRatio(calculateRatio(new BigDecimal(todayUsers), new BigDecimal(lastWeekUsers)));

        // 昨日同期新增用户数
        LambdaQueryWrapper<User> yesterdayUserWrapper = new LambdaQueryWrapper<>();
        yesterdayUserWrapper.between(User::getCreateTime, yesterdayStart, yesterdayEnd);
        Long yesterdayUsers = userService.count(yesterdayUserWrapper);
        vo.setUsersDayRatio(calculateRatio(new BigDecimal(todayUsers), new BigDecimal(yesterdayUsers)));

        // 计算订单数同比
        // 上周同期新增订单数
        LambdaQueryWrapper<Order> lastWeekOrderWrapper = new LambdaQueryWrapper<>();
        lastWeekOrderWrapper.between(Order::getCreateTime, lastWeekStart, lastWeekEnd);
        Long lastWeekOrders = orderService.count(lastWeekOrderWrapper);
        Long todayOrders = vo.getTodayNewOrders() != null ? vo.getTodayNewOrders() : 0L;
        vo.setOrdersWeekRatio(calculateRatio(new BigDecimal(todayOrders), new BigDecimal(lastWeekOrders)));

        // 昨日同期新增订单数
        LambdaQueryWrapper<Order> yesterdayOrderWrapper = new LambdaQueryWrapper<>();
        yesterdayOrderWrapper.between(Order::getCreateTime, yesterdayStart, yesterdayEnd);
        Long yesterdayOrders = orderService.count(yesterdayOrderWrapper);
        vo.setOrdersDayRatio(calculateRatio(new BigDecimal(todayOrders), new BigDecimal(yesterdayOrders)));

        // 计算跑腿员数同比
        // 上周同期新增跑腿员数
        LambdaQueryWrapper<RunnerInfo> lastWeekRunnerWrapper = new LambdaQueryWrapper<>();
        lastWeekRunnerWrapper.eq(RunnerInfo::getCertStatus, 2);
        lastWeekRunnerWrapper.between(RunnerInfo::getCreateTime, lastWeekStart, lastWeekEnd);
        Long lastWeekRunners = runnerInfoService.count(lastWeekRunnerWrapper);
        Long todayRunners = vo.getTodayNewRunners() != null ? vo.getTodayNewRunners() : 0L;
        vo.setRunnersWeekRatio(calculateRatio(new BigDecimal(todayRunners), new BigDecimal(lastWeekRunners)));

        // 昨日同期新增跑腿员数
        LambdaQueryWrapper<RunnerInfo> yesterdayRunnerWrapper = new LambdaQueryWrapper<>();
        yesterdayRunnerWrapper.eq(RunnerInfo::getCertStatus, 2);
        yesterdayRunnerWrapper.between(RunnerInfo::getCreateTime, yesterdayStart, yesterdayEnd);
        Long yesterdayRunners = runnerInfoService.count(yesterdayRunnerWrapper);
        vo.setRunnersDayRatio(calculateRatio(new BigDecimal(todayRunners), new BigDecimal(yesterdayRunners)));
    }

    /**
     * 获取指定时间范围内的收入
     */
    private BigDecimal getRevenueBetween(LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        // 收入统计统一排除「待支付」与「已取消」
        wrapper.ne(Order::getStatus, 1)
                .ne(Order::getStatus, 5);
        wrapper.between(Order::getCompleteTime, start, end);
        List<Order> orders = orderService.list(wrapper);
        return orders.stream()
                .map(Order::getServiceFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算同比（百分比）
     * @param current 当前值
     * @param previous 同期值
     * @return 同比百分比（正数表示增长，负数表示下降）
     */
    private BigDecimal calculateRatio(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            // 如果同期值为0，当前值大于0则返回100%，否则返回0
            return current.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100") : BigDecimal.ZERO;
        }
        // 同比 = (当前值 - 同期值) / 同期值 * 100
        BigDecimal diff = current.subtract(previous);
        return diff.divide(previous, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
