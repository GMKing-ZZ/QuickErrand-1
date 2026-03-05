package com.quickerrand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quickerrand.dto.AddressDTO;
import com.quickerrand.entity.Address;
import com.quickerrand.exception.BusinessException;
import com.quickerrand.mapper.AddressMapper;
import com.quickerrand.service.AddressService;
import com.quickerrand.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址Service实现类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAddress(Long userId, AddressDTO addressDTO) {
        // 如果设置为默认地址，先取消其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            cancelOtherDefaultAddress(userId);
        }

        // 创建地址
        Address address = BeanUtil.copyProperties(addressDTO, Address.class);
        address.setUserId(userId);
        
        // 转换经纬度：String -> BigDecimal
        if (StringUtils.hasText(addressDTO.getLongitude())) {
            try {
                address.setLongitude(new BigDecimal(addressDTO.getLongitude()));
            } catch (NumberFormatException e) {
                log.warn("经度格式错误：{}", addressDTO.getLongitude());
            }
        }
        if (StringUtils.hasText(addressDTO.getLatitude())) {
            try {
                address.setLatitude(new BigDecimal(addressDTO.getLatitude()));
            } catch (NumberFormatException e) {
                log.warn("纬度格式错误：{}", addressDTO.getLatitude());
            }
        }

        // 如果没有设置默认地址，检查是否是第一个地址
        if (address.getIsDefault() == null || address.getIsDefault() == 0) {
            long count = count(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
            if (count == 0) {
                // 第一个地址自动设为默认
                address.setIsDefault(1);
            } else {
                address.setIsDefault(0);
            }
        }

        save(address);
        log.info("用户{}添加地址成功，地址ID：{}", userId, address.getId());
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        // 查询地址
        Address address = getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        // 如果设置为默认地址，先取消其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            cancelOtherDefaultAddress(userId);
        }

        // 更新地址
        BeanUtil.copyProperties(addressDTO, address, "id", "userId");
        
        // 转换经纬度：String -> BigDecimal
        if (StringUtils.hasText(addressDTO.getLongitude())) {
            try {
                address.setLongitude(new BigDecimal(addressDTO.getLongitude()));
            } catch (NumberFormatException e) {
                log.warn("经度格式错误：{}", addressDTO.getLongitude());
            }
        } else {
            address.setLongitude(null);
        }
        if (StringUtils.hasText(addressDTO.getLatitude())) {
            try {
                address.setLatitude(new BigDecimal(addressDTO.getLatitude()));
            } catch (NumberFormatException e) {
                log.warn("纬度格式错误：{}", addressDTO.getLatitude());
            }
        } else {
            address.setLatitude(null);
        }
        
        updateById(address);
        log.info("用户{}更新地址成功，地址ID：{}", userId, addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long userId, Long addressId) {
        // 查询地址
        Address address = getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        // 删除地址
        removeById(addressId);

        // 如果删除的是默认地址，将第一个地址设为默认
        if (address.getIsDefault() == 1) {
            Address firstAddress = getOne(new LambdaQueryWrapper<Address>()
                    .eq(Address::getUserId, userId)
                    .orderByAsc(Address::getCreateTime)
                    .last("LIMIT 1"));
            if (firstAddress != null) {
                firstAddress.setIsDefault(1);
                updateById(firstAddress);
            }
        }

        log.info("用户{}删除地址成功，地址ID：{}", userId, addressId);
    }

    @Override
    public List<AddressVO> getAddressList(Long userId) {
        List<Address> addressList = list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime));

        return addressList.stream()
                .map(address -> {
                    AddressVO vo = BeanUtil.copyProperties(address, AddressVO.class);
                    // 转换经纬度：BigDecimal -> String
                    if (address.getLongitude() != null) {
                        vo.setLongitude(address.getLongitude().toString());
                    }
                    if (address.getLatitude() != null) {
                        vo.setLatitude(address.getLatitude().toString());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public AddressVO getAddressDetail(Long userId, Long addressId) {
        Address address = getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        AddressVO vo = BeanUtil.copyProperties(address, AddressVO.class);
        // 转换经纬度：BigDecimal -> String
        if (address.getLongitude() != null) {
            vo.setLongitude(address.getLongitude().toString());
        }
        if (address.getLatitude() != null) {
            vo.setLatitude(address.getLatitude().toString());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        // 查询地址
        Address address = getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        // 取消其他默认地址
        cancelOtherDefaultAddress(userId);

        // 设置为默认地址
        address.setIsDefault(1);
        updateById(address);
        log.info("用户{}设置默认地址成功，地址ID：{}", userId, addressId);
    }

    /**
     * 取消其他默认地址
     *
     * @param userId 用户ID
     */
    private void cancelOtherDefaultAddress(Long userId) {
        update(new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .set(Address::getIsDefault, 0));
    }
}
