package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quickerrand.config.OrderFeeProperties;
import com.quickerrand.entity.OrderType;
import com.quickerrand.mapper.OrderTypeMapper;
import com.quickerrand.service.OrderTypeService;
import com.quickerrand.vo.OrderTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单类型Service实现类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Service
public class OrderTypeServiceImpl extends ServiceImpl<OrderTypeMapper, OrderType> implements OrderTypeService {

    @Autowired
    private OrderFeeProperties orderFeeProperties;

    @Override
    // 暂时禁用缓存，确保获取最新数据
    // @Cacheable(value = "orderTypeList", key = "'all'")
    public List<OrderTypeVO> getOrderTypeList() {
        List<OrderType> orderTypeList = list(new LambdaQueryWrapper<OrderType>()
                .eq(OrderType::getStatus, 1)
                .orderByAsc(OrderType::getSortOrder));

        log.info("查询到的订单类型数量: {}", orderTypeList.size());
        for (OrderType orderType : orderTypeList) {
            log.info("订单类型详情 - id: {}, typeName: {}, typeIcon: {}, typeDesc: {}, sortOrder: {}, status: {}", 
                    orderType.getId(), orderType.getTypeName(), orderType.getTypeIcon(), 
                    orderType.getTypeDesc(), orderType.getSortOrder(), orderType.getStatus());
        }

        List<OrderTypeVO> result = orderTypeList.stream()
                .map(orderType -> {
                    OrderTypeVO vo = new OrderTypeVO();
                    vo.setId(orderType.getId());
                    vo.setTypeName(orderType.getTypeName());
                    vo.setIcon(orderType.getTypeIcon());
                    vo.setDescription(orderType.getTypeDesc());
                    // 价格从配置读取，避免调整时修改代码
                    vo.setBasePrice(orderFeeProperties.getBasePrice());
                    vo.setPricePerKm(orderFeeProperties.getPricePerKm());
                    vo.setSort(orderType.getSortOrder());
                    vo.setStatus(orderType.getStatus());
                    
                    log.info("转换后的VO - id: {}, typeName: {}, icon: {}, description: {}, sort: {}, status: {}", 
                            vo.getId(), vo.getTypeName(), vo.getIcon(), vo.getDescription(), vo.getSort(), vo.getStatus());
                    
                    return vo;
                })
                .collect(Collectors.toList());
        
        log.info("最终返回的VO列表数量: {}", result.size());
        return result;
    }

}
