package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper接口
 *
 * @author 周政
 * @date 2026-01-26
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
