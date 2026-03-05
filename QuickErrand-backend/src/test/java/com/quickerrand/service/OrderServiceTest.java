package com.quickerrand.service;

import com.quickerrand.dto.CalculateFeeDTO;
import com.quickerrand.dto.CreateOrderDTO;
import com.quickerrand.entity.Order;
import com.quickerrand.entity.OrderType;
import com.quickerrand.entity.User;
import com.quickerrand.exception.BusinessException;
import com.quickerrand.mapper.OrderMapper;
import com.quickerrand.service.impl.OrderServiceImpl;
import com.quickerrand.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单Service单元测试
 *
 * @author 周政
 * @date 2026-01-26
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderTypeService orderTypeService;

    @Mock
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderType mockOrderType;
    private User mockUser;
    private Order mockOrder;

    @BeforeEach
    public void setUp() {
        // 初始化模拟数据
        mockOrderType = new OrderType();
        mockOrderType.setId(1L);
        mockOrderType.setTypeName("帮我买");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setNickname("测试用户");
        mockUser.setPhone("13800138000");
        mockUser.setBalance(new BigDecimal("100.00"));

        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setOrderNo("ORD20260126120000123456");
        mockOrder.setUserId(1L);
        mockOrder.setOrderTypeId(1L);
        mockOrder.setPickupAddress("北京市朝阳区xxx路1号");
        mockOrder.setDeliveryAddress("北京市海淀区xxx路2号");
        mockOrder.setDistance(new BigDecimal("3.5").intValue());
        mockOrder.setPickupCode("123456");
        mockOrder.setStatus(1);
        mockOrder.setCreateTime(LocalDateTime.now());
    }

    @Test
    public void testCalculateFee() {
        // 准备测试数据
        CalculateFeeDTO calculateFeeDTO = new CalculateFeeDTO();
        calculateFeeDTO.setOrderTypeId(1L);
        calculateFeeDTO.setDistance(new BigDecimal("3.5"));

        // 模拟依赖方法
        when(orderTypeService.getById(1L)).thenReturn(mockOrderType);

        // 执行测试
        BigDecimal fee = orderService.calculateFee(calculateFeeDTO);

        // 验证结果：5.00 + 2.00 * 3.5 = 12.00
        assertEquals(new BigDecimal("12.00"), fee);
        verify(orderTypeService, times(1)).getById(1L);
    }

    @Test
    public void testCalculateFee_OrderTypeNotFound() {
        // 准备测试数据
        CalculateFeeDTO calculateFeeDTO = new CalculateFeeDTO();
        calculateFeeDTO.setOrderTypeId(999L);
        calculateFeeDTO.setDistance(new BigDecimal("3.5"));

        // 模拟依赖方法
        when(orderTypeService.getById(999L)).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            orderService.calculateFee(calculateFeeDTO);
        });
    }

    @Test
    public void testCancelOrder_Success() {
        // 准备测试数据
        Long userId = 1L;
        Long orderId = 1L;
        String reason = "不需要了";

        // 模拟依赖方法
        when(orderService.getById(orderId)).thenReturn(mockOrder);
        when(userService.getById(userId)).thenReturn(mockUser);

        // 执行测试
        orderService.cancelOrder(userId, orderId, reason);

        // 验证订单状态已更新为已取消
        assertEquals(5, mockOrder.getStatus());
        assertEquals(reason, mockOrder.getCancelReason());
        assertNotNull(mockOrder.getCancelTime());

        // 验证余额已退回
        assertEquals(new BigDecimal("112.00"), mockUser.getBalance());
    }

    @Test
    public void testCancelOrder_OrderNotFound() {
        // 准备测试数据
        Long userId = 1L;
        Long orderId = 999L;
        String reason = "不需要了";

        // 模拟依赖方法
        when(orderService.getById(orderId)).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(userId, orderId, reason);
        });
    }

    @Test
    public void testCancelOrder_InvalidStatus() {
        // 准备测试数据
        Long userId = 1L;
        Long orderId = 1L;
        String reason = "不需要了";

        // 设置订单状态为配送中（不允许取消）
        mockOrder.setStatus(3);

        // 模拟依赖方法
        when(orderService.getById(orderId)).thenReturn(mockOrder);

        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(userId, orderId, reason);
        });
    }

    @Test
    public void printAdminPassword() {
        String raw = "admin123";
        String encoded = passwordEncoder.encode(raw);
        System.out.println(encoded);
    }

}

