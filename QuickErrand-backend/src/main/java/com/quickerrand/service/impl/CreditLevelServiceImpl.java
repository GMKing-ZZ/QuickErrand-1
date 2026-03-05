package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.entity.Evaluation;
import com.quickerrand.entity.Order;
import com.quickerrand.entity.RunnerInfo;
import com.quickerrand.entity.User;
import com.quickerrand.mapper.EvaluationMapper;
import com.quickerrand.mapper.OrderMapper;
import com.quickerrand.mapper.RunnerInfoMapper;
import com.quickerrand.mapper.UserMapper;
import com.quickerrand.service.CreditLevelService;
import com.quickerrand.vo.RunnerCreditVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 信用等级服务实现类
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Service
public class CreditLevelServiceImpl implements CreditLevelService {

    @Autowired
    private RunnerInfoMapper runnerInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Override
    public Page<RunnerCreditVO> getRunnerCreditList(Integer pageNum, Integer pageSize, String keyword, Integer creditLevel) {
        // 查询已认证的跑腿员
        LambdaQueryWrapper<RunnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RunnerInfo::getCertStatus, 2); // 已认证

        if (creditLevel != null) {
            wrapper.eq(RunnerInfo::getCreditLevel, creditLevel);
        }

        wrapper.orderByDesc(RunnerInfo::getCreditLevel)
                .orderByDesc(RunnerInfo::getTotalOrders);

        Page<RunnerInfo> page = new Page<>(pageNum, pageSize);
        Page<RunnerInfo> runnerPage = runnerInfoMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<RunnerCreditVO> voPage = new Page<>(pageNum, pageSize, runnerPage.getTotal());
        List<RunnerCreditVO> voList = new ArrayList<>();

        // 批量查询用户信息，避免N+1问题
        List<Long> userIds = runnerPage.getRecords().stream()
                .map(RunnerInfo::getUserId)
                .collect(Collectors.toList());

        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
        }

        for (RunnerInfo runner : runnerPage.getRecords()) {
            RunnerCreditVO vo = new RunnerCreditVO();
            BeanUtils.copyProperties(runner, vo);

            // 从Map中获取用户手机号
            User user = userMap.get(runner.getUserId());
            if (user != null) {
                vo.setPhone(user.getPhone());
            }

            // 关键词搜索过滤
            if (keyword != null && !keyword.isEmpty()) {
                if ((vo.getRealName() != null && vo.getRealName().contains(keyword)) ||
                    (vo.getPhone() != null && vo.getPhone().contains(keyword))) {
                    voList.add(vo);
                }
            } else {
                voList.add(vo);
            }
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Integer calculateCreditLevel(Long runnerId) {
        // 这里的 runnerId 实际上传入的是 RunnerInfo 的主键ID
        RunnerInfo runner = runnerInfoMapper.selectById(runnerId);
        if (runner == null) {
            return 1; // 默认1星
        }

        // 订单表与评价表中的 runnerId 字段存的是“跑腿员用户ID”，不是 RunnerInfo 主键
        Long runnerUserId = runner.getUserId();
        if (runnerUserId == null) {
            return 1;
        }

        // 统计完成订单数（t_order.runner_id = 跑腿员用户ID 且 status=4）
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getRunnerId, runnerUserId)
                .eq(Order::getStatus, 4); // 已完成
        Long completedOrders = orderMapper.selectCount(orderWrapper);

        // 统计好评率（使用服务质量评分和服务态度评分的平均值）
        LambdaQueryWrapper<Evaluation> reviewWrapper = new LambdaQueryWrapper<>();
        reviewWrapper.eq(Evaluation::getRunnerId, runnerUserId)
                .eq(Evaluation::getStatus, 0); // 只统计正常状态的评价
        List<Evaluation> evaluations = evaluationMapper.selectList(reviewWrapper);
        Long totalReviews = (long) evaluations.size();

        BigDecimal goodRate = BigDecimal.ZERO;
        if (totalReviews > 0) {
            // 统计平均评分4星及以上的评价（服务质量和服务态度的平均值）
            long goodReviews = evaluations.stream()
                    .filter(eval -> {
                        if (eval.getServiceScore() == null || eval.getAttitudeScore() == null) {
                            return false;
                        }
                        BigDecimal avgScore = new BigDecimal(eval.getServiceScore() + eval.getAttitudeScore())
                                .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
                        return avgScore.compareTo(new BigDecimal(4)) >= 0;
                    })
                    .count();

            goodRate = new BigDecimal(goodReviews)
                    .divide(new BigDecimal(totalReviews), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }

        // 根据完成订单数和好评率计算信用等级
        int creditLevel = 1;

        if (completedOrders >= 100 && goodRate.compareTo(new BigDecimal(95)) >= 0) {
            creditLevel = 5; // 5星：100单以上且好评率95%以上
        } else if (completedOrders >= 50 && goodRate.compareTo(new BigDecimal(90)) >= 0) {
            creditLevel = 4; // 4星：50单以上且好评率90%以上
        } else if (completedOrders >= 20 && goodRate.compareTo(new BigDecimal(85)) >= 0) {
            creditLevel = 3; // 3星：20单以上且好评率85%以上
        } else if (completedOrders >= 5 && goodRate.compareTo(new BigDecimal(80)) >= 0) {
            creditLevel = 2; // 2星：5单以上且好评率80%以上
        }

        log.info("计算跑腿员信用等级，runnerInfoId={}，runnerUserId={}，completedOrders={}，goodRate={}，creditLevel={}",
                runnerId, runnerUserId, completedOrders, goodRate, creditLevel);

        return creditLevel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCreditLevel(Long runnerId, Integer creditLevel) {
        // 这里的 runnerId 也是 RunnerInfo 主键
        RunnerInfo runner = runnerInfoMapper.selectById(runnerId);
        if (runner == null) {
            throw new RuntimeException("跑腿员不存在");
        }

        Long runnerUserId = runner.getUserId();
        if (runnerUserId == null) {
            throw new RuntimeException("跑腿员用户ID不存在");
        }

        // 同时更新完成订单数和好评率（与 calculateCreditLevel 保持一致）
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getRunnerId, runnerUserId)
                .eq(Order::getStatus, 4);
        Long completedOrders = orderMapper.selectCount(orderWrapper);

        LambdaQueryWrapper<Evaluation> reviewWrapper = new LambdaQueryWrapper<>();
        reviewWrapper.eq(Evaluation::getRunnerId, runnerUserId)
                .eq(Evaluation::getStatus, 0); // 只统计正常状态的评价
        List<Evaluation> evaluations = evaluationMapper.selectList(reviewWrapper);
        Long totalReviews = (long) evaluations.size();

        BigDecimal goodRate = BigDecimal.ZERO;
        if (totalReviews > 0) {
            // 统计平均评分4星及以上的评价
            long goodReviews = evaluations.stream()
                    .filter(eval -> {
                        if (eval.getServiceScore() == null || eval.getAttitudeScore() == null) {
                            return false;
                        }
                        BigDecimal avgScore = new BigDecimal(eval.getServiceScore() + eval.getAttitudeScore())
                                .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
                        return avgScore.compareTo(new BigDecimal(4)) >= 0;
                    })
                    .count();

            goodRate = new BigDecimal(goodReviews)
                    .divide(new BigDecimal(totalReviews), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }

        runner.setCreditLevel(creditLevel);
        runner.setTotalOrders(completedOrders.intValue());
        runner.setGoodRate(goodRate);
        runnerInfoMapper.updateById(runner);

        log.info("更新跑腿员信用等级成功，runnerInfoId={}，runnerUserId={}，creditLevel={}", runnerId, runnerUserId, creditLevel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recalculateAllCreditLevels() {
        // 查询所有已认证的跑腿员
        LambdaQueryWrapper<RunnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RunnerInfo::getCertStatus, 2);
        List<RunnerInfo> runners = runnerInfoMapper.selectList(wrapper);

        for (RunnerInfo runner : runners) {
            Integer creditLevel = calculateCreditLevel(runner.getId());
            updateCreditLevel(runner.getId(), creditLevel);
        }

        log.info("批量重新计算信用等级完成，共处理{}个跑腿员", runners.size());
    }
}
