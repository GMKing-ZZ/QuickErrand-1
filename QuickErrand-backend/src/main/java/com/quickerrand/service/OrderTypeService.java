package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.quickerrand.entity.OrderType;
import com.quickerrand.vo.OrderTypeVO;

import java.util.List;

/**
 * 订单类型Service接口
 *
 * @author 周政
 * @date 2026-01-26
 */
public interface OrderTypeService extends IService<OrderType> {

    /**
     * 获取订单类型列表
     *
     * @return 订单类型列表
     */
    List<OrderTypeVO> getOrderTypeList();

}
