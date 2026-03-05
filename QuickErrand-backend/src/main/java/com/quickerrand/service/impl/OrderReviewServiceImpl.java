package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quickerrand.entity.Evaluation;
import com.quickerrand.entity.Order;
import com.quickerrand.entity.RunnerInfo;
import com.quickerrand.entity.User;
import com.quickerrand.mapper.OrderReviewMapper;
import com.quickerrand.service.OrderReviewService;
import com.quickerrand.service.OrderService;
import com.quickerrand.service.RunnerInfoService;
import com.quickerrand.service.UserService;
import com.quickerrand.vo.OrderReviewListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单评价Service实现类（已迁移到 Evaluation）
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Service
public class OrderReviewServiceImpl extends ServiceImpl<OrderReviewMapper, Evaluation> implements OrderReviewService {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RunnerInfoService runnerInfoService;

    @Override
    public Page<OrderReviewListVO> getReviewList(Integer pageNum, Integer pageSize, String keyword, Integer rating, Integer status) {
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();

        // 状态过滤
        if (status != null) {
            wrapper.eq(Evaluation::getStatus, status);
        } else {
            // 默认只查询正常状态的评价
            wrapper.eq(Evaluation::getStatus, 0);
        }

        // 按创建时间降序排列
        wrapper.orderByDesc(Evaluation::getCreateTime);

        Page<Evaluation> page = new Page<>(pageNum, pageSize);
        page(page, wrapper);

        // 转换为VO
        Page<OrderReviewListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<OrderReviewListVO> voList = new ArrayList<>();

        // 批量查询订单、用户、跑腿员信息，避免N+1问题
        List<Long> orderIds = page.getRecords().stream()
                .map(Evaluation::getOrderId)
                .collect(Collectors.toList());
        List<Long> userIds = page.getRecords().stream()
                .map(Evaluation::getUserId)
                .collect(Collectors.toList());
        List<Long> runnerUserIds = page.getRecords().stream()
                .map(Evaluation::getRunnerId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        // 批量查询订单
        Map<Long, Order> orderMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            List<Order> orders = orderService.listByIds(orderIds);
            orderMap = orders.stream()
                    .collect(Collectors.toMap(Order::getId, order -> order));
        }

        // 批量查询用户
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
        }

        // 批量查询跑腿员
        Map<Long, RunnerInfo> runnerMap = new HashMap<>();
        if (!runnerUserIds.isEmpty()) {
            LambdaQueryWrapper<RunnerInfo> runnerWrapper = new LambdaQueryWrapper<>();
            runnerWrapper.in(RunnerInfo::getUserId, runnerUserIds);
            List<RunnerInfo> runners = runnerInfoService.list(runnerWrapper);
            runnerMap = runners.stream()
                    .collect(Collectors.toMap(RunnerInfo::getUserId, runner -> runner));
        }

        for (Evaluation evaluation : page.getRecords()) {
            // 计算平均评分（服务质量和服务态度的平均值）
            Integer avgRating = null;
            if (evaluation.getServiceScore() != null && evaluation.getAttitudeScore() != null) {
                BigDecimal avg = new BigDecimal(evaluation.getServiceScore() + evaluation.getAttitudeScore())
                        .divide(new BigDecimal(2), 0, RoundingMode.HALF_UP);
                avgRating = avg.intValue();
            }

            // 如果指定了评分过滤，且平均分不匹配，则跳过
            if (rating != null && (avgRating == null || !avgRating.equals(rating))) {
                continue;
            }

            OrderReviewListVO vo = new OrderReviewListVO();
            vo.setId(evaluation.getId());
            vo.setOrderId(evaluation.getOrderId());
            vo.setUserId(evaluation.getUserId());
            vo.setRunnerId(evaluation.getRunnerId());
            vo.setServiceScore(evaluation.getServiceScore());
            vo.setAttitudeScore(evaluation.getAttitudeScore());
            vo.setContent(evaluation.getContent());
            vo.setImages(evaluation.getImages());
            vo.setTags(evaluation.getTags());
            vo.setIsAnonymous(0); // Evaluation 没有匿名字段，默认为0
            vo.setStatus(evaluation.getStatus());
            vo.setCreateTime(evaluation.getCreateTime());

            // 从Map中获取订单信息
            Order order = orderMap.get(evaluation.getOrderId());
            if (order != null) {
                vo.setOrderNo(order.getOrderNo());
            }

            // 从Map中获取用户信息
            User user = userMap.get(evaluation.getUserId());
            if (user != null) {
                // 优先使用昵称，然后使用用户名，最后显示未知用户
                if (user.getNickname() != null && !user.getNickname().isEmpty()) {
                    vo.setUserNickname(user.getNickname());
                } else if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                    vo.setUserNickname(user.getUsername());
                } else {
                    vo.setUserNickname("未知用户");
                }
            } else {
                vo.setUserNickname("未知用户");
            }

            // 从Map中获取跑腿员信息
            if (evaluation.getRunnerId() != null) {
                RunnerInfo runnerInfo = runnerMap.get(evaluation.getRunnerId());
                if (runnerInfo != null) {
                    vo.setRunnerName(runnerInfo.getRealName());
                }
            }

            // 关键词搜索（订单编号、用户昵称、跑腿员姓名、评价内容）
            if (keyword != null && !keyword.isEmpty()) {
                String kw = keyword.toLowerCase();
                boolean match = false;
                if (vo.getOrderNo() != null && vo.getOrderNo().toLowerCase().contains(kw)) {
                    match = true;
                }
                if (vo.getUserNickname() != null && vo.getUserNickname().toLowerCase().contains(kw)) {
                    match = true;
                }
                if (vo.getRunnerName() != null && vo.getRunnerName().toLowerCase().contains(kw)) {
                    match = true;
                }
                if (vo.getContent() != null && vo.getContent().toLowerCase().contains(kw)) {
                    match = true;
                }
                if (match) {
                    voList.add(vo);
                }
            } else {
                voList.add(vo);
            }
        }

        voPage.setRecords(voList);
        log.info("管理员查询评价列表，条件：rating={}, status={}, keyword={}, 数量：{}", rating, status, keyword, voList.size());
        return voPage;
    }

    @Override
    public Page<OrderReviewListVO> getUserReviewList(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getUserId, userId);
        wrapper.eq(Evaluation::getStatus, 0);
        wrapper.orderByDesc(Evaluation::getCreateTime);

        Page<Evaluation> page = new Page<>(pageNum, pageSize);
        page(page, wrapper);

        Page<OrderReviewListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<OrderReviewListVO> voList = new ArrayList<>();

        List<Long> orderIds = page.getRecords().stream()
                .map(Evaluation::getOrderId)
                .collect(Collectors.toList());
        List<Long> runnerUserIds = page.getRecords().stream()
                .map(Evaluation::getRunnerId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        Map<Long, Order> orderMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            List<Order> orders = orderService.listByIds(orderIds);
            orderMap = orders.stream()
                    .collect(Collectors.toMap(Order::getId, order -> order));
        }

        Map<Long, RunnerInfo> runnerMap = new HashMap<>();
        if (!runnerUserIds.isEmpty()) {
            LambdaQueryWrapper<RunnerInfo> runnerWrapper = new LambdaQueryWrapper<>();
            runnerWrapper.in(RunnerInfo::getUserId, runnerUserIds);
            List<RunnerInfo> runners = runnerInfoService.list(runnerWrapper);
            runnerMap = runners.stream()
                    .collect(Collectors.toMap(RunnerInfo::getUserId, runner -> runner));
        }

        for (Evaluation evaluation : page.getRecords()) {
            OrderReviewListVO vo = new OrderReviewListVO();
            vo.setId(evaluation.getId());
            vo.setOrderId(evaluation.getOrderId());
            vo.setUserId(evaluation.getUserId());
            vo.setRunnerId(evaluation.getRunnerId());
            vo.setServiceScore(evaluation.getServiceScore());
            vo.setAttitudeScore(evaluation.getAttitudeScore());
            vo.setContent(evaluation.getContent());
            vo.setImages(evaluation.getImages());
            vo.setTags(evaluation.getTags());
            vo.setIsAnonymous(0);
            vo.setStatus(evaluation.getStatus());
            vo.setCreateTime(evaluation.getCreateTime());

            Order order = orderMap.get(evaluation.getOrderId());
            if (order != null) {
                vo.setOrderNo(order.getOrderNo());
            }

            if (evaluation.getRunnerId() != null) {
                RunnerInfo runnerInfo = runnerMap.get(evaluation.getRunnerId());
                if (runnerInfo != null) {
                    vo.setRunnerName(runnerInfo.getRealName());
                }
            }

            voList.add(vo);
        }

        voPage.setRecords(voList);
        log.info("用户查询评价列表，userId={}, 数量：{}", userId, voList.size());
        return voPage;
    }
}
