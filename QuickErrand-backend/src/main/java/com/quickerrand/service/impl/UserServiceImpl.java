package com.quickerrand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quickerrand.dto.ChangePasswordDTO;
import com.quickerrand.dto.CreateUserDTO;
import com.quickerrand.dto.LoginDTO;
import com.quickerrand.dto.RegisterDTO;
import com.quickerrand.dto.ResetPasswordDTO;
import com.quickerrand.dto.UpdateUserInfoDTO;
import com.quickerrand.dto.UserQueryDTO;
import com.quickerrand.dto.WxLoginDTO;
import com.quickerrand.entity.RunnerInfo;
import com.quickerrand.entity.User;
import com.quickerrand.exception.BusinessException;
import com.quickerrand.mapper.AddressMapper;
import com.quickerrand.mapper.UserMapper;
import com.quickerrand.service.SmsService;
import com.quickerrand.service.UserService;
import com.quickerrand.service.RunnerInfoService;
import com.quickerrand.utils.JwtUtils;
import com.quickerrand.vo.LoginVO;
import com.quickerrand.vo.UserDetailVO;
import com.quickerrand.vo.UserListVO;
import com.quickerrand.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 用户Service实现类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SmsService smsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AddressMapper addressMapper;

    @Lazy
    @Autowired
    private RunnerInfoService runnerInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 验证验证码
        boolean valid = smsService.verifyCode(registerDTO.getPhone(), registerDTO.getCode());
        if (!valid) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 检查手机号是否已注册
        User existUser = getByPhone(registerDTO.getPhone());
        if (existUser != null) {
            throw new BusinessException("该手机号已注册");
        }

        // 创建用户
        User user = new User();
        user.setPhone(registerDTO.getPhone());

        // 使用BCrypt加密密码
        String encryptedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(encryptedPassword);

        // 生成用户名（格式：user + 手机号后6位 + 4位随机数）
        String username = generateUsername(registerDTO.getPhone());
        user.setUsername(username);

        // 设置昵称
        if (registerDTO.getNickname() != null && !registerDTO.getNickname().isEmpty()) {
            user.setNickname(registerDTO.getNickname());
        } else {
            user.setNickname("用户" + registerDTO.getPhone().substring(7));
        }

        // 设置用户类型：前端可传 1-普通用户、2-跑腿员，非法或未传时默认普通用户
        Integer userType = registerDTO.getUserType();
        if (userType != null && (userType == 1 || userType == 2)) {
            user.setUserType(userType);
        } else {
            user.setUserType(1); // 默认为普通用户
        }

        // 设置默认值
        user.setStatus(1); // 正常状态
        user.setBalance(BigDecimal.ZERO);
        user.setGender(0); // 未知

        // 保存用户
        save(user);

        log.info("用户注册成功，手机号：{}", registerDTO.getPhone());
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        User user = getByPhone(loginDTO.getPhone());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        boolean valid = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        if (!valid) {
            throw new BusinessException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getPhone());

        // 构建返回数据
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        LoginVO loginVO = new LoginVO(token, userVO);

        log.info("用户登录成功，手机号：{}", loginDTO.getPhone());
        return loginVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO wxLogin(WxLoginDTO wxLoginDTO) {
        // TODO: 调用微信接口获取openid
        // 这里暂时模拟，实际需要调用微信API
        // 实际应该使用 wxLoginDTO.getCode() 调用微信接口获取 openid
        String openid = "mock_openid_" + System.currentTimeMillis();
        
        // 如果提供了 code，应该调用微信接口获取 openid
        // 示例：通过 code 获取 openid 的逻辑
        // String openid = wechatService.getOpenidByCode(wxLoginDTO.getCode());
        
        log.info("微信登录请求，code：{}", wxLoginDTO.getCode());

        // 查询用户
        User user = getByOpenid(openid);

        // 如果用户不存在，创建新用户
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname("微信用户" + System.currentTimeMillis());
            user.setUserType(1);
            user.setStatus(1);
            user.setBalance(BigDecimal.ZERO);
            user.setGender(0);
            // phone 字段现在允许为 null，不需要设置

            // 如果提供了手机号，绑定手机号并生成用户名
            if (wxLoginDTO.getPhone() != null && !wxLoginDTO.getPhone().trim().isEmpty()) {
                // 检查手机号是否已被其他用户使用
                User existUser = getByPhone(wxLoginDTO.getPhone());
                if (existUser != null && !existUser.getOpenid().equals(openid)) {
                    throw new BusinessException("该手机号已被其他账号绑定");
                }
                
                // 验证验证码
                if (wxLoginDTO.getSmsCode() == null || wxLoginDTO.getSmsCode().trim().isEmpty()) {
                    throw new BusinessException("绑定手机号需要验证码");
                }
                boolean valid = smsService.verifyCode(wxLoginDTO.getPhone(), wxLoginDTO.getSmsCode());
                if (!valid) {
                    throw new BusinessException("验证码错误或已过期");
                }
                user.setPhone(wxLoginDTO.getPhone().trim());
                // 生成用户名
                String username = generateUsername(wxLoginDTO.getPhone());
                user.setUsername(username);
            } else {
                // 如果没有手机号，使用 openid 生成用户名
                String username = "wx_" + openid.substring(Math.max(0, openid.length() - 8));
                // 确保用户名唯一
                int retryCount = 0;
                while (getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) != null && retryCount < 10) {
                    username = "wx_" + openid.substring(Math.max(0, openid.length() - 8)) + "_" + retryCount;
                    retryCount++;
                }
                user.setUsername(username);
            }

            save(user);
            log.info("微信用户首次登录，创建新用户，openid：{}，phone：{}", openid, user.getPhone());
        } else {
            // 如果用户已存在，检查是否需要更新手机号
            if (wxLoginDTO.getPhone() != null && !wxLoginDTO.getPhone().trim().isEmpty() 
                && (user.getPhone() == null || user.getPhone().trim().isEmpty())) {
                // 检查手机号是否已被其他用户使用
                User existUser = getByPhone(wxLoginDTO.getPhone());
                if (existUser != null && !existUser.getOpenid().equals(openid)) {
                    throw new BusinessException("该手机号已被其他账号绑定");
                }
                
                // 验证验证码
                if (wxLoginDTO.getSmsCode() == null || wxLoginDTO.getSmsCode().trim().isEmpty()) {
                    throw new BusinessException("绑定手机号需要验证码");
                }
                boolean valid = smsService.verifyCode(wxLoginDTO.getPhone(), wxLoginDTO.getSmsCode());
                if (!valid) {
                    throw new BusinessException("验证码错误或已过期");
                }
                user.setPhone(wxLoginDTO.getPhone().trim());
                // 如果用户没有用户名，生成一个
                if (user.getUsername() == null || user.getUsername().startsWith("wx_")) {
                    String username = generateUsername(wxLoginDTO.getPhone());
                    user.setUsername(username);
                }
                updateById(user);
                log.info("微信用户绑定手机号，openid：{}，phone：{}", openid, user.getPhone());
            }
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成Token，使用 phone 或 openid 作为标识
        String identifier = user.getPhone() != null && !user.getPhone().trim().isEmpty() 
            ? user.getPhone() 
            : (user.getOpenid() != null ? user.getOpenid() : String.valueOf(user.getId()));
        String token = jwtUtils.generateToken(user.getId(), identifier);

        // 构建返回数据
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setHasPassword(user.getPassword() != null && !user.getPassword().isEmpty());
        LoginVO loginVO = new LoginVO(token, userVO);

        log.info("微信用户登录成功，openid：{}，phone：{}", openid, user.getPhone());
        return loginVO;
    }

    @Override
    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return getOne(wrapper);
    }

    @Override
    public User getByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return getOne(wrapper);
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        if (user.getBirthday() != null) {
            userVO.setBirthday(user.getBirthday().toString());
        }
        // 设置是否设置了密码
        userVO.setHasPassword(user.getPassword() != null && !user.getPassword().isEmpty());
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, UpdateUserInfoDTO updateUserInfoDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 处理用户名更新
        if (updateUserInfoDTO.getUsername() != null && !updateUserInfoDTO.getUsername().isEmpty()) {
            String newUsername = updateUserInfoDTO.getUsername().trim();
            
            // 如果新用户名与当前用户名不同，需要验证唯一性
            if (!newUsername.equals(user.getUsername())) {
                // 检查用户名是否已被其他用户使用
                User existUser = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, newUsername));
                if (existUser != null && !existUser.getId().equals(userId)) {
                    throw new BusinessException("该用户名已被使用");
                }
                
                user.setUsername(newUsername);
                log.info("用户{}修改用户名为：{}", userId, newUsername);
            }
        }

        // 更新用户信息
        if (updateUserInfoDTO.getNickname() != null && !updateUserInfoDTO.getNickname().isEmpty()) {
            user.setNickname(updateUserInfoDTO.getNickname());
        }
        if (updateUserInfoDTO.getAvatar() != null) {
            user.setAvatar(updateUserInfoDTO.getAvatar());
        }
        if (updateUserInfoDTO.getGender() != null) {
            user.setGender(updateUserInfoDTO.getGender());
        }

        // 处理生日
        if (updateUserInfoDTO.getBirthday() != null && !updateUserInfoDTO.getBirthday().isEmpty()) {
            try {
                LocalDate birthday = LocalDate.parse(updateUserInfoDTO.getBirthday(), DateTimeFormatter.ISO_LOCAL_DATE);
                user.setBirthday(birthday);
            } catch (Exception e) {
                throw new BusinessException("生日格式不正确，请使用yyyy-MM-dd格式");
            }
        }

        // 处理收货码开关设置
        if (updateUserInfoDTO.getPickupCodeEnabled() != null) {
            user.setPickupCodeEnabled(updateUserInfoDTO.getPickupCodeEnabled() ? 1 : 0);
        }

        // 处理手机号更新
        if (updateUserInfoDTO.getPhone() != null && !updateUserInfoDTO.getPhone().isEmpty()) {
            String newPhone = updateUserInfoDTO.getPhone().trim();
            
            // 如果新手机号与当前手机号不同，需要验证
            if (!newPhone.equals(user.getPhone())) {
                // 验证验证码
                if (updateUserInfoDTO.getCode() == null || updateUserInfoDTO.getCode().isEmpty()) {
                    throw new BusinessException("修改手机号需要验证码");
                }
                boolean valid = smsService.verifyCode(newPhone, updateUserInfoDTO.getCode());
                if (!valid) {
                    throw new BusinessException("验证码错误或已过期");
                }
                
                // 检查新手机号是否已被其他用户使用
                User existUser = getByPhone(newPhone);
                if (existUser != null && !existUser.getId().equals(userId)) {
                    throw new BusinessException("该手机号已被其他账号绑定");
                }
                
                user.setPhone(newPhone);
                log.info("用户{}修改手机号为：{}", userId, newPhone);
            }
        }

        updateById(user);
        log.info("用户{}更新个人信息成功", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证新密码
        if (changePasswordDTO.getNewPassword() == null || changePasswordDTO.getNewPassword().isEmpty()) {
            throw new BusinessException("新密码不能为空");
        }

        // 检查用户是否已有密码
        boolean hasPassword = user.getPassword() != null && !user.getPassword().isEmpty();
        
        if (hasPassword) {
            // 用户有密码，需要验证旧密码
            if (changePasswordDTO.getOldPassword() == null || changePasswordDTO.getOldPassword().isEmpty()) {
                throw new BusinessException("请输入旧密码");
            }
            boolean valid = passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword());
            if (!valid) {
                throw new BusinessException("旧密码错误");
            }
        }
        // 如果用户没有密码，则不需要验证旧密码，直接设置新密码

        // 更新密码
        String encryptedPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        user.setPassword(encryptedPassword);
        updateById(user);

        log.info("用户{}修改密码成功", userId);
    }

    @Override
    public User getUserByUsername(String username) {
        // 支持通过 username 或 phone 登录
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(User::getUsername, username).or().eq(User::getPhone, username));
        return getOne(wrapper);
    }

    /**
     * 生成用户名
     * 格式：user + 手机号后6位 + 4位随机数
     *
     * @param phone 手机号
     * @return 用户名
     */
    private String generateUsername(String phone) {
        String phoneSuffix = phone.length() >= 6 ? phone.substring(phone.length() - 6) : phone;
        Random random = new Random();
        int randomNum = 1000 + random.nextInt(9000); // 生成1000-9999的随机数
        String username = "user" + phoneSuffix + randomNum;
        
        // 检查用户名是否已存在，如果存在则重新生成
        int retryCount = 0;
        while (getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) != null && retryCount < 10) {
            randomNum = 1000 + random.nextInt(9000);
            username = "user" + phoneSuffix + randomNum;
            retryCount++;
        }
        
        return username;
    }

    @Override
    public User getUserById(Long userId) {
        return getById(userId);
    }

    @Override
    public Page<UserListVO> getUserList(UserQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（用户名、昵称、手机号）
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w
                    .like(User::getNickname, queryDTO.getKeyword())
                    .or().like(User::getPhone, queryDTO.getKeyword())
            );
        }

        // 用户类型筛选
        if (queryDTO.getUserType() != null) {
            wrapper.eq(User::getUserType, queryDTO.getUserType());
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(User::getStatus, queryDTO.getStatus());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(User::getCreateTime);

        // 分页查询
        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        page(page, wrapper);

        // 转换为VO
        Page<UserListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(user -> {
            UserListVO vo = new UserListVO();
            BeanUtil.copyProperties(user, vo);

            // 设置用户类型文本
            switch (user.getUserType()) {
                case 1:
                    vo.setUserTypeText("普通用户");
                    break;
                case 2:
                    vo.setUserTypeText("跑腿员");
                    break;
                case 3:
                    vo.setUserTypeText("管理员");
                    break;
            }

            // 设置状态文本
            vo.setStatusText(user.getStatus() == 1 ? "正常" : "禁用");

            return vo;
        }).collect(java.util.stream.Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 不允许禁用管理员
        if (user.getUserType() == 3 && status == 0) {
            throw new BusinessException("不允许禁用管理员账号");
        }

        user.setStatus(status);
        updateById(user);

        log.info("更新用户{}状态为{}", userId, status == 1 ? "正常" : "禁用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(CreateUserDTO createUserDTO) {
        // 检查用户名是否已存在
        User existUserByUsername = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, createUserDTO.getUsername()));
        if (existUserByUsername != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        User existUserByPhone = getByPhone(createUserDTO.getPhone());
        if (existUserByPhone != null) {
            throw new BusinessException("手机号已注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setPhone(createUserDTO.getPhone());
        user.setNickname(createUserDTO.getNickname());
        user.setUserType(createUserDTO.getUserType());
        user.setStatus(createUserDTO.getStatus());
        user.setBalance(BigDecimal.ZERO);
        
        // 设置头像，如果没有提供则使用默认头像
        if (createUserDTO.getAvatar() != null && !createUserDTO.getAvatar().isEmpty()) {
            user.setAvatar(createUserDTO.getAvatar());
        } else {
            // 默认头像
            user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        }
        
        // 设置性别
        user.setGender(createUserDTO.getGender() != null ? createUserDTO.getGender() : 0);
        
        // 设置生日
        if (createUserDTO.getBirthday() != null && !createUserDTO.getBirthday().isEmpty()) {
            try {
                LocalDate birthday = LocalDate.parse(createUserDTO.getBirthday(), DateTimeFormatter.ISO_LOCAL_DATE);
                user.setBirthday(birthday);
            } catch (Exception e) {
                log.warn("生日格式不正确，忽略：{}", createUserDTO.getBirthday());
            }
        }

        // 使用BCrypt加密密码
        String encryptedPassword = passwordEncoder.encode(createUserDTO.getPassword());
        user.setPassword(encryptedPassword);

        // 保存用户
        save(user);

        log.info("管理员创建用户成功，用户名：{}", createUserDTO.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getUserType() == 3) {
            throw new BusinessException("不允许删除管理员账号");
        }

        addressMapper.delete(new LambdaQueryWrapper<com.quickerrand.entity.Address>()
                .eq(com.quickerrand.entity.Address::getUserId, userId));

        baseMapper.physicalDelete(userId);
        log.info("删除用户成功，用户ID：{}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUsers(java.util.List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的用户");
        }

        for (Long userId : ids) {
            User user = getById(userId);
            if (user != null && user.getUserType() == 3) {
                throw new BusinessException("不允许删除管理员账号");
            }
        }

        addressMapper.delete(new LambdaQueryWrapper<com.quickerrand.entity.Address>()
                .in(com.quickerrand.entity.Address::getUserId, ids));

        baseMapper.batchPhysicalDelete(ids);
        log.info("批量删除用户成功，用户ID列表：{}", ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCurrentUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            throw new BusinessException("已设置密码的用户不能通过此方式删除");
        }

        if (user.getUserType() == 3) {
            throw new BusinessException("不允许删除管理员账号");
        }

        baseMapper.physicalDelete(userId);
        log.info("删除未设置密码的用户成功，用户ID：{}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 验证验证码
        boolean valid = smsService.verifyCode(resetPasswordDTO.getPhone(), resetPasswordDTO.getCode());
        if (!valid) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 查询用户
        User user = getByPhone(resetPasswordDTO.getPhone());
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系客服");
        }

        // 更新密码
        String encryptedPassword = passwordEncoder.encode(resetPasswordDTO.getNewPassword());
        user.setPassword(encryptedPassword);
        updateById(user);

        log.info("用户重置密码成功，手机号：{}", resetPasswordDTO.getPhone());
    }

    @Override
    public UserDetailVO getUserDetail(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserDetailVO vo = new UserDetailVO();
        BeanUtil.copyProperties(user, vo);

        switch (user.getUserType()) {
            case 1:
                vo.setUserTypeText("普通用户");
                break;
            case 2:
                vo.setUserTypeText("跑腿员");
                break;
            case 3:
                vo.setUserTypeText("管理员");
                break;
        }

        vo.setStatusText(user.getStatus() == 1 ? "正常" : "禁用");

        switch (user.getGender()) {
            case 1:
                vo.setGenderText("男");
                break;
            case 2:
                vo.setGenderText("女");
                break;
            default:
                vo.setGenderText("未知");
        }

        if (user.getBirthday() != null) {
            vo.setBirthday(user.getBirthday().toString());
        }

        if (user.getUserType() == 2) {
            RunnerInfo runnerInfo = runnerInfoService.getOne(
                new LambdaQueryWrapper<RunnerInfo>().eq(RunnerInfo::getUserId, userId)
            );
            if (runnerInfo != null) {
                UserDetailVO.RunnerInfoVO runnerVO = new UserDetailVO.RunnerInfoVO();
                BeanUtil.copyProperties(runnerInfo, runnerVO);
                
                switch (runnerInfo.getCertStatus()) {
                    case 0:
                        runnerVO.setCertStatusText("未认证");
                        break;
                    case 1:
                        runnerVO.setCertStatusText("审核中");
                        break;
                    case 2:
                        runnerVO.setCertStatusText("已认证");
                        break;
                    case 3:
                        runnerVO.setCertStatusText("已驳回");
                        break;
                }
                
                runnerVO.setCertTime(runnerInfo.getCreateTime());
                vo.setRunnerInfo(runnerVO);
            }
        }

        return vo;
    }

}
