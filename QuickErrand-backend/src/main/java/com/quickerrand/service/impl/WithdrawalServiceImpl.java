package com.quickerrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.exception.BusinessException;
import com.quickerrand.dto.WithdrawalApplyDTO;
import com.quickerrand.dto.WithdrawalAuditDTO;
import com.quickerrand.entity.EarningsRecord;
import com.quickerrand.entity.WithdrawalRecord;
import com.quickerrand.entity.Order;
import com.quickerrand.entity.User;
import com.quickerrand.mapper.EarningsRecordMapper;
import com.quickerrand.mapper.WithdrawalRecordMapper;
import com.quickerrand.mapper.OrderMapper;
import com.quickerrand.service.UserService;
import com.quickerrand.service.WithdrawalService;
import com.quickerrand.vo.WithdrawalRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提现服务实现
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    @Autowired
    private WithdrawalRecordMapper withdrawalRecordMapper;

    @Autowired
    private EarningsRecordMapper earningsRecordMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserService userService;

    /**
     * 提现手续费率（1%）
     */
    private static final BigDecimal FEE_RATE = new BigDecimal("0.01");

    /**
     * 最低提现金额
     */
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("10.00");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyWithdrawal(Long userId, WithdrawalApplyDTO applyDTO) {
        // 验证提现金额
        if (applyDTO.getAmount().compareTo(MIN_AMOUNT) < 0) {
            throw new BusinessException("提现金额不能低于" + MIN_AMOUNT + "元");
        }

        // ===== 计算可提现金额：与 EarningsServiceImpl 中的逻辑保持一致，保证前后端显示一致 =====
        // 查询该跑腿员的所有收益记录（用于统计历史提现、结算记录）
        List<EarningsRecord> allRecords = earningsRecordMapper.selectList(
                new LambdaQueryWrapper<EarningsRecord>()
                        .eq(EarningsRecord::getUserId, userId)
        );

        // 查询该跑腿员的所有已完成订单，用于实时统计订单收益
        List<Order> completedOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getRunnerId, userId)
                        .eq(Order::getStatus, 4)
        );

        // 所有已完成订单的跑腿员收益
        BigDecimal totalOrderEarnings = completedOrders.stream()
                .map(o -> o.getRunnerFee() == null ? BigDecimal.ZERO : o.getRunnerFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 已提现金额（历史提现记录，金额为正数）
        BigDecimal withdrawnAmount = allRecords.stream()
                .filter(r -> r.getType() == 3)
                .map(EarningsRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 如果系统中已经存在非提现的收益记录（订单收益/奖励），说明有明确的结算状态，优先使用结算记录
        boolean hasNonWithdrawRecords = allRecords.stream().anyMatch(r -> r.getType() != 3);

        BigDecimal settledEarnings;
        if (hasNonWithdrawRecords) {
            settledEarnings = allRecords.stream()
                    .filter(r -> r.getType() != 3 && r.getStatus() == 2)
                    .map(EarningsRecord::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            // 当前尚无结算记录，则将所有已完成订单收益视为已结算
            settledEarnings = totalOrderEarnings;
        }

        // 可提现金额 = 已结算收益 - 已提现金额
        BigDecimal withdrawableAmount = settledEarnings.subtract(withdrawnAmount);

        // 验证余额是否充足
        if (applyDTO.getAmount().compareTo(withdrawableAmount) > 0) {
            throw new BusinessException("可提现金额不足，当前可提现：" + withdrawableAmount.setScale(2, RoundingMode.HALF_UP) + "元");
        }

        // 计算手续费和实际到账金额
        BigDecimal fee = applyDTO.getAmount().multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualAmount = applyDTO.getAmount().subtract(fee);

        // 创建提现记录
        WithdrawalRecord record = new WithdrawalRecord();
        record.setUserId(userId);
        record.setAmount(applyDTO.getAmount());
        record.setFee(fee);
        record.setActualAmount(actualAmount);
        record.setAccountType(applyDTO.getAccountType());
        record.setAccountInfo(applyDTO.getAccountInfo());
        record.setStatus(1); // 待审核
        withdrawalRecordMapper.insert(record);

        // 创建提现收益记录（正数，代表本次提现金额）
        EarningsRecord earningsRecord = new EarningsRecord();
        earningsRecord.setUserId(userId);
        earningsRecord.setAmount(applyDTO.getAmount());
        earningsRecord.setType(3); // 提现
        earningsRecord.setStatus(2); // 已结算
        earningsRecord.setRemark("提现申请，提现金额：" + applyDTO.getAmount() + "元");
        earningsRecordMapper.insert(earningsRecord);

        log.info("用户{}申请提现，金额：{}元，手续费：{}元，实际到账：{}元",
                userId, applyDTO.getAmount(), fee, actualAmount);
    }

    @Override
    public List<WithdrawalRecordVO> getWithdrawalRecords(Long userId) {
        // 查询提现记录
        List<WithdrawalRecord> records = withdrawalRecordMapper.selectList(
                new LambdaQueryWrapper<WithdrawalRecord>()
                        .eq(WithdrawalRecord::getUserId, userId)
                        .orderByDesc(WithdrawalRecord::getCreateTime)
        );

        // 转换为VO
        return records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WithdrawalRecordVO> getAdminWithdrawalPage(Integer pageNum, Integer pageSize, Integer status, String keyword) {
        Page<WithdrawalRecord> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<WithdrawalRecord> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(WithdrawalRecord::getStatus, status);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> {
                // 账户信息模糊匹配
                w.like(WithdrawalRecord::getAccountInfo, keyword);

                // 如果关键字是数字，也尝试按用户ID精确匹配
                try {
                    Long userId = Long.parseLong(keyword);
                    w.or().eq(WithdrawalRecord::getUserId, userId);
                } catch (NumberFormatException ignored) {
                }
            });
        }

        wrapper.orderByDesc(WithdrawalRecord::getCreateTime);

        withdrawalRecordMapper.selectPage(page, wrapper);

        Page<WithdrawalRecordVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<WithdrawalRecordVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditWithdrawal(WithdrawalAuditDTO auditDTO) {
        WithdrawalRecord record = withdrawalRecordMapper.selectById(auditDTO.getId());
        if (record == null) {
            throw new BusinessException("提现记录不存在");
        }
        if (record.getStatus() == null || record.getStatus() != 1) {
            throw new BusinessException("当前状态不允许审核");
        }

        Integer status = auditDTO.getStatus();
        if (status == null || (status != 2 && status != 3)) {
            throw new BusinessException("审核结果不合法");
        }

        record.setStatus(status);
        record.setAuditTime(LocalDateTime.now());

        if (status == 3) {
            if (!StringUtils.hasText(auditDTO.getRejectReason())) {
                throw new BusinessException("驳回原因不能为空");
            }
            record.setRejectReason(auditDTO.getRejectReason());
        } else {
            // 通过时清空驳回原因
            record.setRejectReason(null);
        }

        withdrawalRecordMapper.updateById(record);

        log.info("管理员审核提现记录，id={}，结果={}",
                record.getId(), status == 2 ? "已通过" : "已驳回");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markWithdrawalTransferred(Long id) {
        WithdrawalRecord record = withdrawalRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("提现记录不存在");
        }
        if (record.getStatus() == null || record.getStatus() != 2) {
            throw new BusinessException("只有已通过的提现申请才能标记为已到账");
        }

        record.setStatus(4);
        record.setTransferTime(LocalDateTime.now());
        withdrawalRecordMapper.updateById(record);

        log.info("管理员标记提现已到账，id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWithdrawalRecords(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        for (Long id : ids) {
            WithdrawalRecord record = withdrawalRecordMapper.selectById(id);
            if (record != null) {
                earningsRecordMapper.delete(new LambdaQueryWrapper<EarningsRecord>()
                        .eq(EarningsRecord::getUserId, record.getUserId())
                        .eq(EarningsRecord::getType, 3)
                        .eq(EarningsRecord::getAmount, record.getAmount())
                        .likeRight(EarningsRecord::getCreateTime, record.getCreateTime().toLocalDate().toString()));
            }
        }

        int deleted = withdrawalRecordMapper.deleteBatchIds(ids);
        log.info("管理员批量删除提现记录，ids={}，删除数量={}", ids, deleted);
        return deleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWithdrawalRecord(Long id) {
        WithdrawalRecord record = withdrawalRecordMapper.selectById(id);
        if (record != null) {
            earningsRecordMapper.delete(new LambdaQueryWrapper<EarningsRecord>()
                    .eq(EarningsRecord::getUserId, record.getUserId())
                    .eq(EarningsRecord::getType, 3)
                    .eq(EarningsRecord::getAmount, record.getAmount())
                    .likeRight(EarningsRecord::getCreateTime, record.getCreateTime().toLocalDate().toString()));
        }

        withdrawalRecordMapper.deleteById(id);
        log.info("管理员删除单条提现记录，id={}", id);
    }

    /**
     * WithdrawalRecord 转为 VO，并填充文本字段
     */
    private WithdrawalRecordVO convertToVO(WithdrawalRecord record) {
        WithdrawalRecordVO vo = new WithdrawalRecordVO();
        BeanUtils.copyProperties(record, vo);

        // 设置用户昵称（仅用于管理端展示）
        if (record.getUserId() != null) {
            User user = userService.getById(record.getUserId());
            if (user != null) {
                vo.setUserNickname(user.getNickname());
            }
        }

        // 设置账户类型文本
        if (record.getAccountType() != null) {
            switch (record.getAccountType()) {
                case 1:
                    vo.setAccountTypeText("支付宝");
                    break;
                case 2:
                    vo.setAccountTypeText("微信");
                    break;
                case 3:
                    vo.setAccountTypeText("银行卡");
                    break;
                default:
                    vo.setAccountTypeText("未知");
                    break;
            }
        }

        // 设置状态文本
        if (record.getStatus() != null) {
            switch (record.getStatus()) {
                case 1:
                    vo.setStatusText("待审核");
                    break;
                case 2:
                    vo.setStatusText("已通过");
                    break;
                case 3:
                    vo.setStatusText("已驳回");
                    break;
                case 4:
                    vo.setStatusText("已到账");
                    break;
                default:
                    vo.setStatusText("未知状态");
                    break;
            }
        }

        return vo;
    }
}
