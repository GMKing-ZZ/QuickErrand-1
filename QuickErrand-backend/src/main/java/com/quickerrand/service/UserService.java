package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.quickerrand.dto.ChangePasswordDTO;
import com.quickerrand.dto.LoginDTO;
import com.quickerrand.dto.RegisterDTO;
import com.quickerrand.dto.ResetPasswordDTO;
import com.quickerrand.dto.UpdateUserInfoDTO;
import com.quickerrand.dto.UserQueryDTO;
import com.quickerrand.dto.WxLoginDTO;
import com.quickerrand.entity.User;
import com.quickerrand.vo.LoginVO;
import com.quickerrand.vo.UserDetailVO;
import com.quickerrand.vo.UserListVO;
import com.quickerrand.vo.UserVO;

/**
 * 用户Service接口
 *
 * @author 周政
 * @date 2026-01-26
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 密码登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 微信登录
     */
    LoginVO wxLogin(WxLoginDTO wxLoginDTO);

    /**
     * 根据手机号查询用户
     */
    User getByPhone(String phone);

    /**
     * 根据openid查询用户
     */
    User getByOpenid(String openid);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param updateUserInfoDTO 更新信息
     */
    void updateUserInfo(Long userId, UpdateUserInfoDTO updateUserInfoDTO);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param changePasswordDTO 密码信息
     */
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 用户列表
     */
    Page<UserListVO> getUserList(UserQueryDTO queryDTO);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 管理员创建用户
     *
     * @param createUserDTO 用户信息
     */
    void createUser(com.quickerrand.dto.CreateUserDTO createUserDTO);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     */
    void batchDeleteUsers(java.util.List<Long> ids);

    /**
     * 删除当前用户（用于微信登录后未设置密码的情况）
     * 仅允许删除未设置密码的用户
     *
     * @param userId 用户ID
     */
    void deleteCurrentUser(Long userId);

    /**
     * 重置密码（忘记密码）
     *
     * @param resetPasswordDTO 重置密码信息
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);

    UserDetailVO getUserDetail(Long userId);
}

