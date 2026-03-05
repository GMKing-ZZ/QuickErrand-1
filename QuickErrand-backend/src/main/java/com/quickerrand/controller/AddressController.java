package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.dto.AddressDTO;
import com.quickerrand.service.AddressService;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.AddressVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Api(tags = "地址管理接口")
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @ApiOperation("添加地址")
    @PostMapping
    public Result<Long> addAddress(@Validated @RequestBody AddressDTO addressDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        Long addressId = addressService.addAddress(userId, addressDTO);
        return Result.success(addressId);
    }

    @ApiOperation("更新地址")
    @PutMapping("/{id}")
    public Result<String> updateAddress(@PathVariable Long id, @Validated @RequestBody AddressDTO addressDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        addressService.updateAddress(userId, id, addressDTO);
        return Result.success("更新成功");
    }

    @ApiOperation("删除地址")
    @DeleteMapping("/{id}")
    public Result<String> deleteAddress(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        addressService.deleteAddress(userId, id);
        return Result.success("删除成功");
    }

    @ApiOperation("获取地址列表")
    @GetMapping("/list")
    public Result<List<AddressVO>> getAddressList() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<AddressVO> addressList = addressService.getAddressList(userId);
        return Result.success(addressList);
    }

    @ApiOperation("获取地址详情")
    @GetMapping("/{id}")
    public Result<AddressVO> getAddressDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        AddressVO addressVO = addressService.getAddressDetail(userId, id);
        return Result.success(addressVO);
    }

    @ApiOperation("设置默认地址")
    @PutMapping("/{id}/default")
    public Result<String> setDefaultAddress(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        addressService.setDefaultAddress(userId, id);
        return Result.success("设置成功");
    }
}
