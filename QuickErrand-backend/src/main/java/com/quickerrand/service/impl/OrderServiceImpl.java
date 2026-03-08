package com.quickerrand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quickerrand.dto.CalculateFeeDTO;
import com.quickerrand.dto.CreateOrderDTO;
import com.quickerrand.dto.EvaluateOrderDTO;
import com.quickerrand.dto.OrderQueryDTO;
import com.quickerrand.config.OrderFeeProperties;
import com.quickerrand.entity.Evaluation;
import com.quickerrand.entity.Order;
import com.quickerrand.entity.OrderType;
import com.quickerrand.entity.User;
import com.quickerrand.entity.EarningsRecord;
import com.quickerrand.entity.ChatMessage;
import com.quickerrand.entity.ChatOrderRel;
import com.quickerrand.exception.BusinessException;
import com.quickerrand.mapper.EvaluationMapper;
import com.quickerrand.mapper.OrderMapper;
import com.quickerrand.mapper.EarningsRecordMapper;
import com.quickerrand.mapper.ChatMessageMapper;
import com.quickerrand.mapper.ChatOrderRelMapper;
import com.quickerrand.service.OrderService;
import com.quickerrand.service.OrderTypeService;
import com.quickerrand.service.UserService;
import com.quickerrand.service.RunnerBlacklistService;
import com.quickerrand.vo.OrderListVO;
import com.quickerrand.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.quickerrand.websocket.OrderPushHandler;
import com.quickerrand.service.MessageService;

/**
 * 订单Service实现类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderTypeService orderTypeService;

    @Autowired
    private OrderFeeProperties orderFeeProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private EarningsRecordMapper earningsRecordMapper;

    @Autowired(required = false)
    private OrderPushHandler orderPushHandler;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatOrderRelMapper chatOrderRelMapper;

    @Autowired
    private RunnerBlacklistService runnerBlacklistService;

    @Override
    public BigDecimal calculateFee(CalculateFeeDTO calculateFeeDTO) {
        // 获取订单类型
        OrderType orderType = orderTypeService.getById(calculateFeeDTO.getOrderTypeId());
        if (orderType == null) {
            throw new BusinessException("订单类型不存在");
        }

        // 计算费用：服务费 = 基础费用 + 每公里费用 * 距离（公里）
        BigDecimal distance = calculateFeeDTO.getDistance();
        if (distance == null) {
            distance = BigDecimal.ZERO;
        }

        BigDecimal baseFee = orderFeeProperties.getBasePrice();
        BigDecimal pricePerKm = orderFeeProperties.getPricePerKm();
        BigDecimal totalFee = baseFee.add(pricePerKm.multiply(distance))
            .setScale(orderFeeProperties.getScale(), RoundingMode.HALF_UP);

        log.info("计算订单费用，订单类型：{}，距离：{}公里，费用：{}", orderType.getTypeName(), distance, totalFee);
        return totalFee;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, CreateOrderDTO createOrderDTO) {
        // 获取订单类型
        OrderType orderType = orderTypeService.getById(createOrderDTO.getOrderTypeId());
        if (orderType == null) {
            throw new BusinessException("订单类型不存在");
        }

        // 计算订单费用
        BigDecimal serviceFee;
        // 如果提供了自定义金额，则使用自定义金额；否则根据距离计算
        if (createOrderDTO.getCustomAmount() != null && createOrderDTO.getCustomAmount().compareTo(BigDecimal.ZERO) > 0) {
            serviceFee = createOrderDTO.getCustomAmount();
            log.info("使用自定义金额：{}", serviceFee);
        } else {
            // 根据距离计算费用
            if (createOrderDTO.getDistance() != null) {
                CalculateFeeDTO calculateFeeDTO = new CalculateFeeDTO();
                calculateFeeDTO.setOrderTypeId(createOrderDTO.getOrderTypeId());
                calculateFeeDTO.setDistance(createOrderDTO.getDistance());
                serviceFee = calculateFee(calculateFeeDTO);
            } else {
                // 如果没有提供距离，使用基础费用
                serviceFee = new BigDecimal("5.00");
            }
        }

        // 计算平台费用和跑腿员费用：平台费用 = 服务费用 * 平台抽成比例；跑腿员费用 = 服务费用 - 平台费用
        BigDecimal platformRate = orderFeeProperties.getPlatformRate();
        BigDecimal platformFee = serviceFee.multiply(platformRate)
            .setScale(orderFeeProperties.getScale(), RoundingMode.HALF_UP);
        BigDecimal runnerFee = serviceFee.subtract(platformFee)
            .setScale(orderFeeProperties.getScale(), RoundingMode.HALF_UP);

        // 创建订单
        Order order = new Order();
        BeanUtil.copyProperties(createOrderDTO, order);
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setServiceFee(serviceFee);
        order.setPlatformFee(platformFee);
        order.setRunnerFee(runnerFee);
        
        // 处理收货码逻辑
        boolean enablePickupCode = true;
        if (createOrderDTO.getEnablePickupCode() != null) {
            enablePickupCode = createOrderDTO.getEnablePickupCode();
        } else {
            User user = userService.getById(userId);
            if (user != null && user.getPickupCodeEnabled() != null) {
                enablePickupCode = user.getPickupCodeEnabled() == 1;
            }
        }
        if (enablePickupCode) {
            order.setPickupCode(generatePickupCode());
        } else {
            order.setPickupCode(null);
        }
        
        order.setStatus(1); // 待支付（数据库状态值：1待支付）
        
        // 转换距离单位：如果 DTO 中是公里，转换为米（数据库要求 NOT NULL，必须有值）
        if (createOrderDTO.getDistance() != null && createOrderDTO.getDistance().compareTo(BigDecimal.ZERO) > 0) {
            order.setDistance(createOrderDTO.getDistance().multiply(new BigDecimal("1000")).intValue());
        } else {
            // 如果没有提供距离，设置默认值 0 米
            order.setDistance(0);
            log.warn("订单距离未提供，使用默认值 0 米");
        }
        
        // 转换经纬度：DTO 中是 String 类型，需要转换为 BigDecimal（数据库要求 NOT NULL，必须有值）
        if (createOrderDTO.getPickupLongitude() != null && !createOrderDTO.getPickupLongitude().isEmpty()) {
            try {
                order.setPickupLongitude(new BigDecimal(createOrderDTO.getPickupLongitude()));
            } catch (NumberFormatException e) {
                log.error("取件经度格式错误：{}，无法创建订单", createOrderDTO.getPickupLongitude());
                throw new BusinessException("取件经度格式错误");
            }
        } else {
            log.error("取件经度不能为空，无法创建订单");
            throw new BusinessException("取件经度不能为空，请选择取件位置");
        }
        
        if (createOrderDTO.getPickupLatitude() != null && !createOrderDTO.getPickupLatitude().isEmpty()) {
            try {
                order.setPickupLatitude(new BigDecimal(createOrderDTO.getPickupLatitude()));
            } catch (NumberFormatException e) {
                log.error("取件纬度格式错误：{}，无法创建订单", createOrderDTO.getPickupLatitude());
                throw new BusinessException("取件纬度格式错误");
            }
        } else {
            log.error("取件纬度不能为空，无法创建订单");
            throw new BusinessException("取件纬度不能为空，请选择取件位置");
        }
        
        if (createOrderDTO.getDeliveryLongitude() != null && !createOrderDTO.getDeliveryLongitude().isEmpty()) {
            try {
                order.setDeliveryLongitude(new BigDecimal(createOrderDTO.getDeliveryLongitude()));
            } catch (NumberFormatException e) {
                log.error("收件经度格式错误：{}，无法创建订单", createOrderDTO.getDeliveryLongitude());
                throw new BusinessException("收件经度格式错误");
            }
        } else {
            log.error("收件经度不能为空，无法创建订单");
            throw new BusinessException("收件经度不能为空，请检查送达地址");
        }
        
        if (createOrderDTO.getDeliveryLatitude() != null && !createOrderDTO.getDeliveryLatitude().isEmpty()) {
            try {
                order.setDeliveryLatitude(new BigDecimal(createOrderDTO.getDeliveryLatitude()));
            } catch (NumberFormatException e) {
                log.error("收件纬度格式错误：{}，无法创建订单", createOrderDTO.getDeliveryLatitude());
                throw new BusinessException("收件纬度格式错误");
            }
        } else {
            log.error("收件纬度不能为空，无法创建订单");
            throw new BusinessException("收件纬度不能为空，请检查送达地址");
        }
        
        // 物品描述（数据库要求 NOT NULL，必须有值）
        if (createOrderDTO.getItemDescription() != null && !createOrderDTO.getItemDescription().trim().isEmpty()) {
            order.setItemDescription(createOrderDTO.getItemDescription().trim());
        } else {
            // 如果没有提供物品描述，设置默认值
            order.setItemDescription("");
            log.warn("物品描述未提供，使用空字符串");
        }

        save(order);

        log.info("创建订单成功，订单号：{}，用户ID：{}，服务费用：{}", order.getOrderNo(), userId, serviceFee);
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object payOrder(Long userId, Long orderId) {
        // 查询订单
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确");
        }

        // 模拟支付：当前仅支持“微信/银行卡”模拟支付，不进行余额校验与扣款
        order.setStatus(2); // 待接单（数据库状态值：2待接单）
        order.setPayTime(LocalDateTime.now());
        updateById(order);

        // 推送新订单给跑腿员（与余额支付成功后的逻辑一致）
        if (orderPushHandler != null) {
            OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);
            OrderType orderType = orderTypeService.getById(order.getOrderTypeId());
            if (orderType != null) {
                orderVO.setOrderTypeName(orderType.getTypeName());
            }
            orderVO.setStatusText(getStatusText(order.getStatus()));
            orderPushHandler.pushNewOrder(orderVO);
        }

        // 推送消息给用户：订单支付成功
        messageService.pushMessage(userId, "订单支付成功", 
                "您的订单 " + order.getOrderNo() + " 已支付成功，等待跑腿员接单", 1, orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "模拟支付成功");
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWxPayNotify(String notifyData) {
        // TODO: 解析微信支付回调数据
        // TODO: 验证签名
        // TODO: 更新订单状态

        log.info("处理微信支付回调：{}", notifyData);

        // 模拟处理
        // 实际应该解析notifyData，获取订单号和支付状态
        // 然后更新订单状态为已支付（待接单）
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 获取当前用户信息
        User currentUser = userService.getById(userId);
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 权限检查：
        // 1. 订单创建者可以查看
        // 2. 跑腿员可以查看待接单订单（状态为2）
        boolean canView = false;
        if (order.getUserId().equals(userId)) {
            // 订单创建者
            canView = true;
        } else if (currentUser.getUserType() != null && currentUser.getUserType() == 2 
                && order.getStatus() != null && order.getStatus() == 2) {
            // 跑腿员可以查看待接单订单
            canView = true;
        } else if (order.getRunnerId() != null && order.getRunnerId().equals(userId)) {
            // 已接单的跑腿员可以查看自己接的订单
            canView = true;
        }

        if (!canView) {
            throw new BusinessException("订单不存在");
        }

        OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);
        // 补齐 VO 字段：amount（实付金额）与 distance（公里）
        fillOrderComputedFields(order, orderVO);

        // 获取订单类型名称
        OrderType orderType = orderTypeService.getById(order.getOrderTypeId());
        if (orderType != null) {
            orderVO.setOrderTypeName(orderType.getTypeName());
        }

        // 设置订单状态文本
        orderVO.setStatusText(getStatusText(order.getStatus()));

        // 如果有跑腿员，获取跑腿员信息
        if (order.getRunnerId() != null) {
            User runner = userService.getById(order.getRunnerId());
            if (runner != null) {
                orderVO.setRunnerName(runner.getNickname());
                orderVO.setRunnerPhone(runner.getPhone());
            }
        }

        // 查询该订单的用户评价信息（如果有）
        // 注意：评价的 userId 始终为下单用户的ID，跑腿员查看详情时也需要看到这条评价
        LambdaQueryWrapper<Evaluation> evalWrapper = new LambdaQueryWrapper<>();
        evalWrapper.eq(Evaluation::getOrderId, orderId)
                .eq(Evaluation::getUserId, order.getUserId())
                .eq(Evaluation::getStatus, 0)
                .last("limit 1");
        Evaluation evaluation = evaluationMapper.selectOne(evalWrapper);
        if (evaluation != null) {
            orderVO.setEvaluated(true);
            orderVO.setServiceScore(evaluation.getServiceScore());
            orderVO.setAttitudeScore(evaluation.getAttitudeScore());
            orderVO.setEvaluationContent(evaluation.getContent());
            orderVO.setEvaluationImages(evaluation.getImages());
        } else {
            orderVO.setEvaluated(false);
        }

        // 如果是跑腿员查看待接单订单，检查是否被下单用户拉黑
        if (currentUser.getUserType() != null && currentUser.getUserType() == 2 
                && order.getStatus() != null && order.getStatus() == 2) {
            boolean isBlacklisted = runnerBlacklistService.isBlacklisted(order.getUserId(), userId);
            orderVO.setBlacklisted(isBlacklisted);
        }

        return orderVO;
    }

    @Override
    public List<OrderVO> getOrderList(Long userId, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<Order> queryWrapper =
            new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);

        // 如果指定了状态，添加状态过滤
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Order::getCreateTime);

        // 查询订单列表
        List<Order> orders = list(queryWrapper);

        // 预查询当前用户对这些订单的评价记录，用于标记“是否已评价”，避免 N+1 查询
        List<Long> orderIds = new ArrayList<>();
        for (Order o : orders) {
            orderIds.add(o.getId());
        }
        Map<Long, Evaluation> evaluationMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            LambdaQueryWrapper<Evaluation> evalWrapper = new LambdaQueryWrapper<>();
            evalWrapper.eq(Evaluation::getUserId, userId)
                    .eq(Evaluation::getStatus, 0)
                    .in(Evaluation::getOrderId, orderIds);
            List<Evaluation> evaluations = evaluationMapper.selectList(evalWrapper);
            for (Evaluation e : evaluations) {
                evaluationMap.put(e.getOrderId(), e);
            }
        }

        // 转换为VO
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orders) {
            OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);
            // 补齐 VO 字段：amount（实付金额）与 distance（公里）
            fillOrderComputedFields(order, orderVO);

            // 获取订单类型名称
            OrderType orderType = orderTypeService.getById(order.getOrderTypeId());
            if (orderType != null) {
                orderVO.setOrderTypeName(orderType.getTypeName());
            }

            // 设置订单状态文本
            orderVO.setStatusText(getStatusText(order.getStatus()));

            // 如果有跑腿员，获取跑腿员信息
            if (order.getRunnerId() != null) {
                User runner = userService.getById(order.getRunnerId());
                if (runner != null) {
                    orderVO.setRunnerName(runner.getNickname());
                    orderVO.setRunnerPhone(runner.getPhone());
                }
            }

            // 标记是否已评价
            Evaluation evaluation = evaluationMap.get(order.getId());
            orderVO.setEvaluated(evaluation != null);

            orderVOList.add(orderVO);
        }

        log.info("查询订单列表，用户ID：{}，状态：{}，数量：{}", userId, status, orderVOList.size());
        return orderVOList;
    }

    /**
     * 订单 VO 补齐字段（避免前端出现 null 导致渲染异常）
     * - amount：用户实付金额（使用 service_fee）
     * - distance：将数据库米转换为公里
     */
    private void fillOrderComputedFields(Order order, OrderVO orderVO) {
        // 实付金额：service_fee（runner_fee + platform_fee = service_fee）
        if (order.getServiceFee() != null) {
            orderVO.setAmount(order.getServiceFee());
        }

        // 距离：米 -> 公里
        if (order.getDistance() != null) {
            BigDecimal km = new BigDecimal(order.getDistance())
                .divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
            orderVO.setDistance(km);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId, String reason) {
        // 查询订单
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 只有待支付和待接单状态的订单可以取消（数据库状态值：1待支付，2待接单）
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new BusinessException("当前订单状态不允许取消");
        }

        // 如果订单已支付，需要退款
        if (order.getStatus() == 2 && order.getPayTime() != null) {
            // 余额支付退款
            User user = userService.getById(userId);
            if (user != null) {
                BigDecimal totalFee = order.getServiceFee().add(order.getPlatformFee());
                user.setBalance(user.getBalance().add(totalFee));
                userService.updateById(user);
                log.info("订单取消，退款到余额，订单号：{}，金额：{}", order.getOrderNo(), totalFee);
            }
        }
        // TODO: 微信支付退款处理

        // 更新订单状态
        order.setStatus(5); // 已取消（数据库状态值：5已取消）
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
        updateById(order);

        // 推送消息给用户：订单已取消
        messageService.pushMessage(userId, "订单已取消", 
                "您的订单 " + order.getOrderNo() + " 已取消，原因：" + (reason != null ? reason : "用户主动取消"), 1, orderId);

        log.info("取消订单成功，订单号：{}，原因：{}", order.getOrderNo(), reason);
    }

    @Override
    public List<OrderVO> searchOrders(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getOrderList(userId, null);
        }

        // 构建查询条件
        LambdaQueryWrapper<Order> queryWrapper =
            new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);

        // 搜索订单号、取货地址、收货地址、物品描述
        queryWrapper.and(wrapper -> wrapper
            .like(Order::getOrderNo, keyword)
            .or().like(Order::getPickupAddress, keyword)
            .or().like(Order::getDeliveryAddress, keyword)
            .or().like(Order::getItemDescription, keyword)
        );

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Order::getCreateTime);

        // 查询订单列表
        List<Order> orders = list(queryWrapper);

        // 转换为VO
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orders) {
            OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);

            // 获取订单类型名称
            OrderType orderType = orderTypeService.getById(order.getOrderTypeId());
            if (orderType != null) {
                orderVO.setOrderTypeName(orderType.getTypeName());
            }

            // 设置订单状态文本
            orderVO.setStatusText(getStatusText(order.getStatus()));

            // 如果有跑腿员，获取跑腿员信息
            if (order.getRunnerId() != null) {
                User runner = userService.getById(order.getRunnerId());
                if (runner != null) {
                    orderVO.setRunnerName(runner.getNickname());
                    orderVO.setRunnerPhone(runner.getPhone());
                }
            }

            orderVOList.add(orderVO);
        }

        log.info("搜索订单，用户ID：{}，关键词：{}，结果数量：{}", userId, keyword, orderVOList.size());
        return orderVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateOrder(Long userId, EvaluateOrderDTO evaluateOrderDTO) {
        // 查询订单
        Order order = getById(evaluateOrderDTO.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 只有已完成的订单才能评价
        if (order.getStatus() != 4) {
            throw new BusinessException("只有已完成的订单才能评价");
        }

        // 检查是否已经评价过
        LambdaQueryWrapper<Evaluation> queryWrapper =
            new LambdaQueryWrapper<>();
        queryWrapper.eq(Evaluation::getOrderId, evaluateOrderDTO.getOrderId());
        queryWrapper.eq(Evaluation::getUserId, userId);
        Long count = evaluationMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("该订单已评价，不能重复评价");
        }

        // 检查订单是否有跑腿员
        if (order.getRunnerId() == null) {
            throw new BusinessException("该订单无跑腿员，无法评价");
        }

        // 创建评价记录
        Evaluation evaluation = new Evaluation();
        evaluation.setOrderId(evaluateOrderDTO.getOrderId());
        evaluation.setUserId(userId);
        evaluation.setRunnerId(order.getRunnerId());
        
        // 设置评分（优先使用独立字段，如果只有 score 字段则同时设置两个评分）
        if (evaluateOrderDTO.getServiceScore() != null && evaluateOrderDTO.getAttitudeScore() != null) {
            evaluation.setServiceScore(evaluateOrderDTO.getServiceScore());
            evaluation.setAttitudeScore(evaluateOrderDTO.getAttitudeScore());
        } else if (evaluateOrderDTO.getScore() != null) {
            // 兼容处理：如果只有 score 字段，同时设置两个评分
            evaluation.setServiceScore(evaluateOrderDTO.getScore());
            evaluation.setAttitudeScore(evaluateOrderDTO.getScore());
        } else {
            throw new BusinessException("评分不能为空");
        }
        
        evaluation.setContent(evaluateOrderDTO.getContent());
        evaluation.setImages(evaluateOrderDTO.getImages());
        evaluation.setTags(evaluateOrderDTO.getTags());
        evaluation.setStatus(0); // 正常状态

        evaluationMapper.insert(evaluation);

        log.info("订单评价成功，订单号：{}，服务质量评分：{}，服务态度评分：{}", 
            order.getOrderNo(), evaluation.getServiceScore(), evaluation.getAttitudeScore());
    }

    @Override
    public List<OrderVO> getPendingOrders(Long runnerId) {
        LambdaQueryWrapper<Order> queryWrapper =
            new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getStatus, 2);
        queryWrapper.orderByDesc(Order::getCreateTime);
        List<Order> orders = list(queryWrapper);

        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orders) {
            OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);

            OrderType orderType = orderTypeService.getById(order.getOrderTypeId());
            if (orderType != null) {
                orderVO.setOrderTypeName(orderType.getTypeName());
            }

            orderVO.setStatusText(getStatusText(order.getStatus()));

            if (runnerId != null) {
                orderVO.setBlacklisted(runnerBlacklistService.isBlacklisted(order.getUserId(), runnerId));
            }

            orderVOList.add(orderVO);
        }

        log.info("查询待接单列表，数量：{}", orderVOList.size());
        return orderVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptOrder(Long runnerId, Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 2) {
            throw new BusinessException("该订单不是待接单状态");
        }

        if (order.getRunnerId() != null) {
            throw new BusinessException("该订单已被其他跑腿员接单");
        }

        if (runnerBlacklistService.isBlacklisted(order.getUserId(), runnerId)) {
            throw new BusinessException("您已被该用户拉黑，无法接此订单");
        }

        order.setStatus(3);
        order.setRunnerId(runnerId);
        order.setAcceptTime(LocalDateTime.now());
        updateById(order);

        // 推送消息给下单用户：跑腿员已接单
        User runner = userService.getById(runnerId);
        String runnerName = runner != null && runner.getNickname() != null ? runner.getNickname() : "跑腿员";
        messageService.pushMessage(order.getUserId(), "跑腿员已接单", 
                "您的订单 " + order.getOrderNo() + " 已被 " + runnerName + " 接单，请耐心等待", 1, orderId);

        log.info("跑腿员接单成功，跑腿员ID：{}，订单号：{}", runnerId, order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pickupOrder(Long runnerId, Long orderId) {
        // 查询订单
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单是否属于该跑腿员
        if (!order.getRunnerId().equals(runnerId)) {
            throw new BusinessException("该订单不属于您");
        }

        // 检查订单状态是否为服务中（数据库状态值：3服务中）
        if (order.getStatus() != 3) {
            throw new BusinessException("该订单不是服务中状态");
        }

        // 更新订单状态（取货后仍为服务中状态，直到完成）
        order.setPickupTime(LocalDateTime.now());
        updateById(order);

        // 推送消息给下单用户：跑腿员已取件
        messageService.pushMessage(order.getUserId(), "跑腿员已取件", 
                "您的订单 " + order.getOrderNo() + " 已取件，正在配送中", 1, orderId);

        log.info("跑腿员取件成功，跑腿员ID：{}，订单号：{}", runnerId, order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long runnerId, Long orderId, String pickupCode) {
        // 查询订单
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单是否属于该跑腿员
        if (!order.getRunnerId().equals(runnerId)) {
            throw new BusinessException("该订单不属于您");
        }

        // 检查订单状态是否为服务中（数据库状态值：3服务中）
        if (order.getStatus() != 3) {
            throw new BusinessException("该订单不是服务中状态");
        }

        // 验证收货码（如果订单有收货码）
        if (order.getPickupCode() != null && !order.getPickupCode().isEmpty()) {
            if (pickupCode == null || !order.getPickupCode().equals(pickupCode)) {
                throw new BusinessException("收货码错误");
            }
        }

        // 更新订单状态
        order.setStatus(4); // 已完成
        order.setCompleteTime(LocalDateTime.now());
        updateById(order);

        // 推送消息给下单用户：订单已完成
        messageService.pushMessage(order.getUserId(), "订单已完成", 
                "您的订单 " + order.getOrderNo() + " 已完成，请对跑腿员进行评价", 1, orderId);

        // 推送消息给跑腿员：订单已完成
        messageService.pushMessage(runnerId, "订单已完成", 
                "您服务的订单 " + order.getOrderNo() + " 已完成，收益 " + order.getRunnerFee() + " 元", 1, orderId);

        log.info("跑腿员完成订单，跑腿员ID：{}，订单号：{}", runnerId, order.getOrderNo());

        // 创建订单收益记录（如果尚未创建过），用于跑腿员收益统计
        if (order.getRunnerFee() != null && order.getRunnerFee().compareTo(BigDecimal.ZERO) > 0) {
            LambdaQueryWrapper<EarningsRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EarningsRecord::getUserId, runnerId)
                   .eq(EarningsRecord::getOrderId, orderId)
                   .eq(EarningsRecord::getType, 1);
            Long exists = earningsRecordMapper.selectCount(wrapper);
            if (exists == null || exists == 0L) {
                EarningsRecord record = new EarningsRecord();
                record.setUserId(runnerId);
                record.setOrderId(orderId);
                record.setAmount(order.getRunnerFee());
                record.setType(1);   // 订单收益
                record.setStatus(1); // 待结算
                record.setRemark("订单完成，订单号：" + order.getOrderNo());
                earningsRecordMapper.insert(record);
            }
        }
    }

    @Override
    public List<OrderVO> getRunnerActiveOrders(Long runnerId) {
        // 查询跑腿员的进行中订单（状态为3服务中）
        LambdaQueryWrapper<Order> queryWrapper =
            new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getRunnerId, runnerId);
        queryWrapper.eq(Order::getStatus, 3); // 服务中（数据库状态值：3服务中）
        queryWrapper.orderByAsc(Order::getStatus); // 按状态排序，待取件优先
        queryWrapper.orderByAsc(Order::getCreateTime);
        List<Order> orders = list(queryWrapper);

        // 转换为VO
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orders) {
            OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);

            // 获取订单类型名称
            OrderType orderType = orderTypeService.getById(order.getOrderTypeId());
            if (orderType != null) {
                orderVO.setOrderTypeName(orderType.getTypeName());
            }

            // 设置订单状态文本
            orderVO.setStatusText(getStatusText(order.getStatus()));

            orderVOList.add(orderVO);
        }

        log.info("查询跑腿员进行中订单，跑腿员ID：{}，数量：{}", runnerId, orderVOList.size());
        return orderVOList;
    }

    @Override
    public List<OrderVO> getRunnerCompletedOrders(Long runnerId) {
        // 查询跑腿员的已完成订单（状态为4已完成）
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getRunnerId, runnerId);
        queryWrapper.eq(Order::getStatus, 4); // 已完成
        queryWrapper.orderByDesc(Order::getCompleteTime);
        queryWrapper.orderByDesc(Order::getCreateTime);
        List<Order> orders = list(queryWrapper);

        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orders) {
            OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);

            OrderType orderType = orderTypeService.getById(order.getOrderTypeId());
            if (orderType != null) {
                orderVO.setOrderTypeName(orderType.getTypeName());
            }

            orderVO.setStatusText(getStatusText(order.getStatus()));
            orderVOList.add(orderVO);
        }

        log.info("查询跑腿员已完成订单，跑腿员ID：{}，数量：{}", runnerId, orderVOList.size());
        return orderVOList;
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = RandomUtil.randomNumbers(6);
        return "ORD" + date + random;
    }

    /**
     * 生成收货码
     */
    private String generatePickupCode() {
        return RandomUtil.randomNumbers(6);
    }

    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 1:
                return "待支付";
            case 2:
                return "待接单";
            case 3:
                return "服务中";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            default:
                return "未知状态";
        }
    }

    @Override
    public Page<OrderListVO> getAdminOrderList(OrderQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（订单号、用户手机号、地址）
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w
                .like(Order::getOrderNo, queryDTO.getKeyword())
                .or().like(Order::getPickupAddress, queryDTO.getKeyword())
                .or().like(Order::getDeliveryAddress, queryDTO.getKeyword())
            );
        }

        // 订单状态过滤
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Order::getStatus, queryDTO.getStatus());
        }

        // 订单类型过滤
        if (queryDTO.getOrderTypeId() != null) {
            wrapper.eq(Order::getOrderTypeId, queryDTO.getOrderTypeId());
        }

        // 用户ID过滤
        if (queryDTO.getUserId() != null) {
            wrapper.eq(Order::getUserId, queryDTO.getUserId());
        }

        // 跑腿员ID过滤
        if (queryDTO.getRunnerId() != null) {
            wrapper.eq(Order::getRunnerId, queryDTO.getRunnerId());
        }

        // 时间范围过滤
        if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty()) {
            wrapper.ge(Order::getCreateTime, LocalDateTime.parse(queryDTO.getStartTime() + " 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
            wrapper.le(Order::getCreateTime, LocalDateTime.parse(queryDTO.getEndTime() + " 23:59:59",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(Order::getCreateTime);

        // 分页查询
        Page<Order> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        page(page, wrapper);

        // 转换为VO
        Page<OrderListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<OrderListVO> voList = new ArrayList<>();

        for (Order order : page.getRecords()) {
            OrderListVO vo = new OrderListVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setUserId(order.getUserId());
            vo.setOrderTypeId(order.getOrderTypeId());
            vo.setPickupAddress(order.getPickupAddress());
            vo.setDeliveryAddress(order.getDeliveryAddress());
            vo.setAmount(order.getServiceFee());
            vo.setStatus(order.getStatus());
            vo.setStatusText(getStatusText(order.getStatus()));
            vo.setRunnerId(order.getRunnerId());
            vo.setCreateTime(order.getCreateTime());
            vo.setPaymentTime(order.getPayTime());
            vo.setCompleteTime(order.getCompleteTime());

            // 获取用户信息
            User user = userService.getById(order.getUserId());
            if (user != null) {
                vo.setUserNickname(user.getNickname());
                vo.setUserPhone(user.getPhone());
            }

            // 获取订单类型名称
            OrderType orderType = orderTypeService.getById(order.getOrderTypeId());
            if (orderType != null) {
                vo.setOrderTypeName(orderType.getTypeName());
            }

            // 获取跑腿员信息
            if (order.getRunnerId() != null) {
                User runner = userService.getById(order.getRunnerId());
                if (runner != null) {
                    vo.setRunnerNickname(runner.getNickname());
                    vo.setRunnerPhone(runner.getPhone());
                }
            }

            voList.add(vo);
        }

        voPage.setRecords(voList);
        log.info("管理员查询订单列表，条件：{}，数量：{}", queryDTO, voList.size());
        return voPage;
    }

    @Override
    public OrderVO getAdminOrderDetail(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);
        fillOrderComputedFields(order, orderVO);

        OrderType orderType = orderTypeService.getById(order.getOrderTypeId());
        if (orderType != null) {
            orderVO.setOrderTypeName(orderType.getTypeName());
        }

        orderVO.setStatusText(getStatusText(order.getStatus()));

        if (order.getRunnerId() != null) {
            User runner = userService.getById(order.getRunnerId());
            if (runner != null) {
                orderVO.setRunnerName(runner.getNickname());
                orderVO.setRunnerPhone(runner.getPhone());
            }
        }

        log.info("管理员查询订单详情，订单ID：{}", orderId);
        return orderVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long orderId, Integer status) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证状态值（数据库状态值：1-5）
        if (status < 1 || status > 5) {
            throw new BusinessException("订单状态值不合法");
        }

        // 更新订单状态
        order.setStatus(status);

        // 根据状态更新相应的时间字段
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case 2: // 待接单（支付完成）
                if (order.getPayTime() == null) {
                    order.setPayTime(now);
                }
                break;
            case 3: // 服务中（已接单或已取件）
                if (order.getAcceptTime() == null) {
                    order.setAcceptTime(now);
                }
                if (order.getPickupTime() == null && order.getAcceptTime() != null) {
                    // 如果已接单但未取件，可以设置取件时间
                    // 注意：实际业务中取件时间应该在取件时单独设置
                }
                break;
            case 4: // 已完成
                if (order.getCompleteTime() == null) {
                    order.setCompleteTime(now);
                }
                break;
            case 5: // 已取消
                if (order.getCancelTime() == null) {
                    order.setCancelTime(now);
                }
                break;
        }

        updateById(order);
        log.info("管理员更新订单状态，订单ID：{}，新状态：{}", orderId, status);
    }

    @Override
    public Page<com.quickerrand.vo.OrderReviewListVO> getRunnerReviews(Long runnerId, Integer pageNum, Integer pageSize) {
        // 构建查询条件，查询该跑腿员的评价
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getRunnerId, runnerId);
        wrapper.eq(Evaluation::getStatus, 0); // 正常状态
        wrapper.orderByDesc(Evaluation::getCreateTime);

        // 分页查询
        Page<Evaluation> page = new Page<>(pageNum, pageSize);
        evaluationMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<com.quickerrand.vo.OrderReviewListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<com.quickerrand.vo.OrderReviewListVO> voList = new ArrayList<>();

        // 批量查询订单和用户信息，避免N+1问题
        List<Long> orderIds = page.getRecords().stream()
                .map(Evaluation::getOrderId)
                .collect(Collectors.toList());
        List<Long> userIds = page.getRecords().stream()
                .map(Evaluation::getUserId)
                .collect(Collectors.toList());

        // 批量查询订单
        Map<Long, Order> orderMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            List<Order> orders = this.listByIds(orderIds);
            orderMap = orders.stream()
                    .collect(Collectors.toMap(order -> order.getId(), order -> order));
        }

        // 批量查询用户
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(user -> user.getId(), user -> user));
        }

        for (Evaluation evaluation : page.getRecords()) {
            com.quickerrand.vo.OrderReviewListVO vo = new com.quickerrand.vo.OrderReviewListVO();
            vo.setId(evaluation.getId());
            vo.setOrderId(evaluation.getOrderId());
            vo.setUserId(evaluation.getUserId());
            vo.setRunnerId(evaluation.getRunnerId());
            vo.setServiceScore(evaluation.getServiceScore());
            vo.setAttitudeScore(evaluation.getAttitudeScore());
            vo.setContent(evaluation.getContent());
            vo.setImages(evaluation.getImages());
            vo.setTags(evaluation.getTags());
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

            voList.add(vo);
        }

        voPage.setRecords(voList);
        log.info("查询跑腿员评价列表，跑腿员ID：{}，数量：{}", runnerId, voList.size());
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        chatMessageMapper.delete(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getOrderId, orderId));

        chatOrderRelMapper.delete(new LambdaQueryWrapper<ChatOrderRel>()
                .eq(ChatOrderRel::getOrderId, orderId));

        messageService.deleteByOrderId(orderId);

        removeById(orderId);
        log.info("管理员删除订单，订单ID：{}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteOrders(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            throw new BusinessException("订单ID列表不能为空");
        }

        chatMessageMapper.delete(new LambdaQueryWrapper<ChatMessage>()
                .in(ChatMessage::getOrderId, orderIds));

        chatOrderRelMapper.delete(new LambdaQueryWrapper<ChatOrderRel>()
                .in(ChatOrderRel::getOrderId, orderIds));

        messageService.deleteByOrderIds(orderIds);

        removeByIds(orderIds);
        log.info("管理员批量删除订单，订单ID列表：{}", orderIds);
    }

}
