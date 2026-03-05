package com.quickerrand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quickerrand.dto.RunnerAuthApplyDTO;
import com.quickerrand.dto.RunnerAuthApprovalDTO;
import com.quickerrand.dto.UpdateRunnerServiceDTO;
import com.quickerrand.entity.RunnerInfo;
import com.quickerrand.entity.User;
import com.quickerrand.entity.Order;
import com.quickerrand.entity.Evaluation;
import com.quickerrand.exception.BusinessException;
import com.quickerrand.mapper.RunnerInfoMapper;
import com.quickerrand.mapper.OrderMapper;
import com.quickerrand.mapper.EvaluationMapper;
import com.quickerrand.service.RunnerInfoService;
import com.quickerrand.service.UserService;
import com.quickerrand.vo.RunnerAuthListVO;
import com.quickerrand.vo.RunnerInfoVO;
import com.quickerrand.vo.RunnerPublicInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * 跑腿员信息Service实现类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Service
public class RunnerInfoServiceImpl extends ServiceImpl<RunnerInfoMapper, RunnerInfo> implements RunnerInfoService {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyAuth(Long userId, RunnerAuthApplyDTO applyDTO) {
        // 检查用户是否存在
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查是否已经申请过
        LambdaQueryWrapper<RunnerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RunnerInfo::getUserId, userId);
        RunnerInfo existingInfo = this.getOne(queryWrapper);

        if (existingInfo != null) {
            // 如果正在审核中，不允许重复申请
            if (existingInfo.getCertStatus() == 1) {
                throw new BusinessException("您的认证申请正在审核中，请耐心等待");
            }
            // 如果已认证或被驳回，允许重新申请，更新信息
            existingInfo.setRealName(applyDTO.getRealName());
            existingInfo.setIdCard(applyDTO.getIdCard());
            existingInfo.setIdCardFront(applyDTO.getIdCardFront());
            existingInfo.setIdCardBack(applyDTO.getIdCardBack());
            existingInfo.setCertStatus(1); // 审核中
            existingInfo.setRejectReason(null);
            this.updateById(existingInfo);
            log.info("用户{}重新申请跑腿员认证", userId);
            return;
        }

        // 创建新的认证申请
        RunnerInfo runnerInfo = new RunnerInfo();
        runnerInfo.setUserId(userId);
        runnerInfo.setRealName(applyDTO.getRealName());
        runnerInfo.setIdCard(applyDTO.getIdCard());
        runnerInfo.setIdCardFront(applyDTO.getIdCardFront());
        runnerInfo.setIdCardBack(applyDTO.getIdCardBack());
        runnerInfo.setCertStatus(1); // 审核中
        runnerInfo.setCreditLevel(3); // 默认3星
        runnerInfo.setTotalOrders(0);
        runnerInfo.setGoodRate(new java.math.BigDecimal("100.00"));
        runnerInfo.setServiceRange(5000); // 默认5000米
        this.save(runnerInfo);
        log.info("用户{}申请跑腿员认证", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveAuth(RunnerAuthApprovalDTO approvalDTO) {
        // 检查跑腿员信息是否存在
        RunnerInfo runnerInfo = this.getById(approvalDTO.getRunnerInfoId());
        if (runnerInfo == null) {
            throw new BusinessException("跑腿员信息不存在");
        }

        // 检查状态是否为审核中
        if (runnerInfo.getCertStatus() != 1) {
            throw new BusinessException("该申请不在审核中");
        }

        // 检查审核结果是否合法
        Integer certStatus = approvalDTO.getCertStatus();
        if (certStatus != 2 && certStatus != 3) {
            throw new BusinessException("审核结果不合法");
        }

        // 如果是驳回，必须填写驳回原因
        if (certStatus == 3 && (approvalDTO.getRejectReason() == null || approvalDTO.getRejectReason().trim().isEmpty())) {
            throw new BusinessException("驳回时必须填写驳回原因");
        }

        // 更新认证状态
        runnerInfo.setCertStatus(certStatus);
        runnerInfo.setRejectReason(approvalDTO.getRejectReason());
        if (certStatus == 2) {
            // 认证时间使用createTime字段
            runnerInfo.setCreateTime(LocalDateTime.now());
            // 更新用户类型为跑腿员
            User user = userService.getById(runnerInfo.getUserId());
            if (user != null) {
                user.setUserType(2); // 2表示跑腿员
                userService.updateById(user);
            }
        }
        this.updateById(runnerInfo);
        log.info("审核跑腿员认证，runnerInfoId={}, certStatus={}", approvalDTO.getRunnerInfoId(), certStatus);
    }

    @Override
    public RunnerInfoVO getAuthInfo(Long userId) {
        // 查询跑腿员信息
        LambdaQueryWrapper<RunnerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RunnerInfo::getUserId, userId);
        RunnerInfo runnerInfo = this.getOne(queryWrapper);

        if (runnerInfo == null) {
            return null;
        }

        // 转换为VO
        RunnerInfoVO vo = BeanUtil.copyProperties(runnerInfo, RunnerInfoVO.class);

        // 设置认证状态文本
        switch (runnerInfo.getCertStatus()) {
            case 0:
                vo.setCertStatusText("未认证");
                break;
            case 1:
                vo.setCertStatusText("审核中");
                break;
            case 2:
                vo.setCertStatusText("已认证");
                break;
            case 3:
                vo.setCertStatusText("已驳回");
                break;
            default:
                vo.setCertStatusText("未知");
        }

        // 身份证号脱敏处理
        if (runnerInfo.getIdCard() != null && runnerInfo.getIdCard().length() == 18) {
            String idCard = runnerInfo.getIdCard();
            vo.setIdCard(idCard.substring(0, 6) + "********" + idCard.substring(14));
        }

        // ===== 实时服务统计：根据订单与评价动态计算 =====
        // 注意：订单表和评价表中的 runnerId 字段使用的是跑腿员的用户ID

        // 1. 完成订单数
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getRunnerId, userId)
                .eq(Order::getStatus, 4); // 已完成
        Long completedOrders = orderMapper.selectCount(orderWrapper);

        // 2. 好评率（参考 CreditLevelServiceImpl 的计算规则）
        LambdaQueryWrapper<Evaluation> reviewWrapper = new LambdaQueryWrapper<>();
        reviewWrapper.eq(Evaluation::getRunnerId, userId)
                .eq(Evaluation::getStatus, 0);
        java.util.List<Evaluation> evaluations = evaluationMapper.selectList(reviewWrapper);
        Long totalReviews = (long) evaluations.size();

        BigDecimal goodRate = BigDecimal.ZERO;
        if (totalReviews > 0) {
            long goodReviews = evaluations.stream()
                    .filter(eval -> {
                        if (eval.getServiceScore() == null || eval.getAttitudeScore() == null) {
                            return false;
                        }
                        BigDecimal avgScore = new BigDecimal(eval.getServiceScore() + eval.getAttitudeScore())
                                .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
                        return avgScore.compareTo(new BigDecimal(4)) >= 0;
                    })
                    .count();

            goodRate = new BigDecimal(goodReviews)
                    .divide(new BigDecimal(totalReviews), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // 3. 如果信用等级为空或非法，按完成单量和好评率动态计算一个合理的等级
        Integer creditLevel = runnerInfo.getCreditLevel();
        if (creditLevel == null || creditLevel < 1 || creditLevel > 5) {
            int level = 1;
            if (completedOrders >= 100 && goodRate.compareTo(new BigDecimal("95")) >= 0) {
                level = 5;
            } else if (completedOrders >= 50 && goodRate.compareTo(new BigDecimal("90")) >= 0) {
                level = 4;
            } else if (completedOrders >= 20 && goodRate.compareTo(new BigDecimal("85")) >= 0) {
                level = 3;
            } else if (completedOrders >= 5 && goodRate.compareTo(new BigDecimal("80")) >= 0) {
                level = 2;
            }
            creditLevel = level;
        }

        // 回填到 VO（不强制更新数据库，避免频繁写入）
        vo.setTotalOrders(completedOrders.intValue());
        vo.setGoodRate(goodRate);
        vo.setCreditLevel(creditLevel);

        return vo;
    }

    @Override
    public Page<RunnerAuthListVO> getAuthApplicationList(Integer pageNum, Integer pageSize, Integer certStatus, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<RunnerInfo> wrapper = new LambdaQueryWrapper<>();

        // 认证状态过滤
        if (certStatus != null) {
            wrapper.eq(RunnerInfo::getCertStatus, certStatus);
        }

        // 关键词搜索（真实姓名、身份证号）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                .like(RunnerInfo::getRealName, keyword)
                .or().like(RunnerInfo::getIdCard, keyword)
            );
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(RunnerInfo::getCreateTime);

        // 分页查询
        Page<RunnerInfo> page = new Page<>(pageNum, pageSize);
        page(page, wrapper);

        // 转换为VO
        Page<RunnerAuthListVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        java.util.List<RunnerAuthListVO> voList = new java.util.ArrayList<>();

        for (RunnerInfo runnerInfo : page.getRecords()) {
            RunnerAuthListVO vo = new RunnerAuthListVO();
            vo.setId(runnerInfo.getId());
            vo.setUserId(runnerInfo.getUserId());
            vo.setRealName(runnerInfo.getRealName());
            vo.setIdCard(runnerInfo.getIdCard());
            vo.setIdCardFront(runnerInfo.getIdCardFront());
            vo.setIdCardBack(runnerInfo.getIdCardBack());
            vo.setCertStatus(runnerInfo.getCertStatus());
            vo.setCertTime(runnerInfo.getCreateTime());
            vo.setRejectReason(runnerInfo.getRejectReason());
            vo.setCreateTime(runnerInfo.getCreateTime());

            // 设置认证状态文本
            switch (runnerInfo.getCertStatus()) {
                case 0:
                    vo.setCertStatusText("未认证");
                    break;
                case 1:
                    vo.setCertStatusText("审核中");
                    break;
                case 2:
                    vo.setCertStatusText("已认证");
                    break;
                case 3:
                    vo.setCertStatusText("已驳回");
                    break;
                default:
                    vo.setCertStatusText("未知");
            }

            // 获取用户信息
            User user = userService.getById(runnerInfo.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setPhone(user.getPhone());
            }

            voList.add(vo);
        }

        voPage.setRecords(voList);
        log.info("管理员查询认证申请列表，条件：certStatus={}, keyword={}, 数量：{}", certStatus, keyword, voList.size());
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateServiceSettings(Long userId, UpdateRunnerServiceDTO updateDTO) {
        LambdaQueryWrapper<RunnerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RunnerInfo::getUserId, userId);
        RunnerInfo runnerInfo = this.getOne(queryWrapper);

        if (runnerInfo == null) {
            throw new BusinessException("跑腿员信息不存在");
        }

        if (runnerInfo.getCertStatus() != 2) {
            throw new BusinessException("只有已认证的跑腿员才能修改服务设置");
        }

        String serviceTime = updateDTO.getServiceTime();
        if (serviceTime == null || serviceTime.isEmpty()) {
            throw new BusinessException("服务时间不能为空");
        }

        Pattern timePattern = Pattern.compile("^([01]?[0-9]|2[0-3]):([0-5][0-9])-([01]?[0-9]|2[0-3]):([0-5][0-9])$");
        if (!timePattern.matcher(serviceTime).matches()) {
            throw new BusinessException("服务时间格式不正确，正确格式如：08:00-22:00");
        }

        String[] times = serviceTime.split("-");
        if (times.length != 2) {
            throw new BusinessException("服务时间格式不正确，正确格式如：08:00-22:00");
        }

        String startTime = times[0];
        String endTime = times[1];
        if (startTime.compareTo(endTime) >= 0) {
            throw new BusinessException("开始时间必须早于结束时间");
        }

        runnerInfo.setServiceTime(serviceTime);
        runnerInfo.setServiceRange(updateDTO.getServiceRange());
        this.updateById(runnerInfo);
        log.info("跑腿员{}更新服务设置，服务时间：{}，服务范围：{}米", userId, serviceTime, updateDTO.getServiceRange());
    }

    @Override
    public RunnerPublicInfoVO getRunnerPublicInfo(Long runnerUserId) {
        LambdaQueryWrapper<RunnerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RunnerInfo::getUserId, runnerUserId);
        RunnerInfo runnerInfo = this.getOne(queryWrapper);

        if (runnerInfo == null || runnerInfo.getCertStatus() != 2) {
            throw new BusinessException("跑腿员信息不存在或未认证");
        }

        RunnerPublicInfoVO vo = new RunnerPublicInfoVO();
        vo.setUserId(runnerUserId);

        User user = userService.getById(runnerUserId);
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getRunnerId, runnerUserId)
                .eq(Order::getStatus, 4);
        Long completedOrders = orderMapper.selectCount(orderWrapper);
        vo.setTotalOrders(completedOrders.intValue());

        LambdaQueryWrapper<Evaluation> reviewWrapper = new LambdaQueryWrapper<>();
        reviewWrapper.eq(Evaluation::getRunnerId, runnerUserId)
                .eq(Evaluation::getStatus, 0);
        java.util.List<Evaluation> evaluations = evaluationMapper.selectList(reviewWrapper);
        Long totalReviews = (long) evaluations.size();

        BigDecimal goodRate = BigDecimal.ZERO;
        if (totalReviews > 0) {
            long goodReviews = evaluations.stream()
                    .filter(eval -> {
                        if (eval.getServiceScore() == null || eval.getAttitudeScore() == null) {
                            return false;
                        }
                        BigDecimal avgScore = new BigDecimal(eval.getServiceScore() + eval.getAttitudeScore())
                                .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
                        return avgScore.compareTo(new BigDecimal(4)) >= 0;
                    })
                    .count();

            goodRate = new BigDecimal(goodReviews)
                    .divide(new BigDecimal(totalReviews), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        vo.setGoodRate(goodRate);

        Integer creditLevel = runnerInfo.getCreditLevel();
        if (creditLevel == null || creditLevel < 1 || creditLevel > 5) {
            int level = 1;
            if (completedOrders >= 100 && goodRate.compareTo(new BigDecimal("95")) >= 0) {
                level = 5;
            } else if (completedOrders >= 50 && goodRate.compareTo(new BigDecimal("90")) >= 0) {
                level = 4;
            } else if (completedOrders >= 20 && goodRate.compareTo(new BigDecimal("85")) >= 0) {
                level = 3;
            } else if (completedOrders >= 5 && goodRate.compareTo(new BigDecimal("80")) >= 0) {
                level = 2;
            }
            creditLevel = level;
        }
        vo.setCreditLevel(creditLevel);

        vo.setServiceTime(runnerInfo.getServiceTime());

        vo.setServiceRange(runnerInfo.getServiceRange());
        vo.setCertTime(runnerInfo.getCreateTime());

        log.info("获取跑腿员公开信息，跑腿员ID：{}", runnerUserId);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthApplication(Long runnerInfoId) {
        if (runnerInfoId == null) {
            throw new BusinessException("跑腿员信息ID不能为空");
        }

        RunnerInfo runnerInfo = this.getById(runnerInfoId);
        if (runnerInfo == null) {
            throw new BusinessException("认证申请记录不存在");
        }

        if (runnerInfo.getCertStatus() == 2) {
            User user = userService.getById(runnerInfo.getUserId());
            if (user != null && user.getUserType() == 2) {
                user.setUserType(1);
                userService.updateById(user);
            }
        }

        this.removeById(runnerInfoId);
        log.info("删除认证申请记录，runnerInfoId={}, userId={}", runnerInfoId, runnerInfo.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteAuthApplications(java.util.List<Long> runnerInfoIds) {
        if (runnerInfoIds == null || runnerInfoIds.isEmpty()) {
            throw new BusinessException("删除ID列表不能为空");
        }

        for (Long runnerInfoId : runnerInfoIds) {
            RunnerInfo runnerInfo = this.getById(runnerInfoId);
            if (runnerInfo != null) {
                if (runnerInfo.getCertStatus() == 2) {
                    User user = userService.getById(runnerInfo.getUserId());
                    if (user != null && user.getUserType() == 2) {
                        user.setUserType(1);
                        userService.updateById(user);
                    }
                }
            }
        }

        this.removeByIds(runnerInfoIds);
        log.info("批量删除认证申请记录，数量={}", runnerInfoIds.size());
    }

}
