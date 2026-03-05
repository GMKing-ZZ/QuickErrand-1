package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.quickerrand.dto.AddressDTO;
import com.quickerrand.entity.Address;
import com.quickerrand.vo.AddressVO;

import java.util.List;

/**
 * 地址Service接口
 *
 * @author 周政
 * @date 2026-01-26
 */
public interface AddressService extends IService<Address> {

    /**
     * 添加地址
     *
     * @param userId 用户ID
     * @param addressDTO 地址信息
     * @return 地址ID
     */
    Long addAddress(Long userId, AddressDTO addressDTO);

    /**
     * 更新地址
     *
     * @param userId 用户ID
     * @param addressId 地址ID
     * @param addressDTO 地址信息
     */
    void updateAddress(Long userId, Long addressId, AddressDTO addressDTO);

    /**
     * 删除地址
     *
     * @param userId 用户ID
     * @param addressId 地址ID
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 获取地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    List<AddressVO> getAddressList(Long userId);

    /**
     * 获取地址详情
     *
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 地址详情
     */
    AddressVO getAddressDetail(Long userId, Long addressId);

    /**
     * 设置默认地址
     *
     * @param userId 用户ID
     * @param addressId 地址ID
     */
    void setDefaultAddress(Long userId, Long addressId);
}
