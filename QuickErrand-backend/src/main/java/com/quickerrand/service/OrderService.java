package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.quickerrand.dto.CalculateFeeDTO;
import com.quickerrand.dto.CreateOrderDTO;
import com.quickerrand.dto.OrderQueryDTO;
import com.quickerrand.entity.Order;
import com.quickerrand.vo.OrderListVO;
import com.quickerrand.vo.OrderVO;

import java.math.BigDecimal;

/**
 * 订单Service接口
 *
 * @author 周政
 * @date 2026-01-26
 */
public interface OrderService extends IService<Order> {

    /**
     * 计算订单费用
     *
     * @param calculateFeeDTO 计算参数
     * @return 订单费用
     */
    BigDecimal calculateFee(CalculateFeeDTO calculateFeeDTO);

    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param createOrderDTO 订单信息
     * @return 订单ID
     */
    Long createOrder(Long userId, CreateOrderDTO createOrderDTO);

    /**
     * 支付订单
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 支付信息（微信支付返回支付参数，余额支付直接完成）
     */
    Object payOrder(Long userId, Long orderId);

    /**
     * 微信支付回调处理
     *
     * @param notifyData 回调数据
     */
    void handleWxPayNotify(String notifyData);

    /**
     * 获取订单详情
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getOrderDetail(Long userId, Long orderId);

    /**
     * 获取订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    java.util.List<OrderVO> getOrderList(Long userId, Integer status);

    /**
     * 取消订单
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param reason 取消原因
     */
    void cancelOrder(Long userId, Long orderId, String reason);

    /**
     * 搜索订单
     *
     * @param userId 用户ID
     * @param keyword 搜索关键词（订单号、地址等）
     * @return 订单列表
     */
    java.util.List<OrderVO> searchOrders(Long userId, String keyword);

    /**
     * 评价订单
     *
     * @param userId 用户ID
     * @param evaluateOrderDTO 评价信息
     */
    void evaluateOrder(Long userId, com.quickerrand.dto.EvaluateOrderDTO evaluateOrderDTO);

    /**
     * 获取待接单列表
     *
     * @return 待接单订单列表
     */
    java.util.List<OrderVO> getPendingOrders();

    /**
     * 跑腿员接单
     *
     * @param runnerId 跑腿员ID
     * @param orderId 订单ID
     */
    void acceptOrder(Long runnerId, Long orderId);

    /**
     * 跑腿员更新订单状态（取件）
     *
     * @param runnerId 跑腿员ID
     * @param orderId 订单ID
     */
    void pickupOrder(Long runnerId, Long orderId);

    /**
     * 跑腿员完成订单（送达）
     *
     * @param runnerId 跑腿员ID
     * @param orderId 订单ID
     * @param pickupCode 收货码
     */
    void completeOrder(Long runnerId, Long orderId, String pickupCode);

    /**
     * 获取跑腿员的进行中订单列表
     *
     * @param runnerId 跑腿员ID
     * @return 进行中订单列表
     */
    java.util.List<OrderVO> getRunnerActiveOrders(Long runnerId);

    /**
     * 获取跑腿员的已完成订单列表
     *
     * @param runnerId 跑腿员ID
     * @return 已完成订单列表
     */
    java.util.List<OrderVO> getRunnerCompletedOrders(Long runnerId);

    /**
     * 管理员分页查询订单列表
     *
     * @param queryDTO 查询条件
     * @return 订单列表
     */
    Page<OrderListVO> getAdminOrderList(OrderQueryDTO queryDTO);

    /**
     * 管理员获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getAdminOrderDetail(Long orderId);

    /**
     * 管理员更新订单状态
     *
     * @param orderId 订单ID
     * @param status 订单状态
     */
    void updateOrderStatus(Long orderId, Integer status);

    /**
     * 获取跑腿员的评价列表
     *
     * @param runnerId 跑腿员ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 评价列表
     */
    Page<com.quickerrand.vo.OrderReviewListVO> getRunnerReviews(Long runnerId, Integer pageNum, Integer pageSize);

    /**
     * 管理员删除单个订单
     *
     * @param orderId 订单ID
     */
    void deleteOrder(Long orderId);

    /**
     * 管理员批量删除订单
     *
     * @param orderIds 订单ID列表
     */
    void batchDeleteOrders(java.util.List<Long> orderIds);

}
