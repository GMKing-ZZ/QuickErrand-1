package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址Mapper接口
 *
 * @author 周政
 * @date 2026-01-26
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
