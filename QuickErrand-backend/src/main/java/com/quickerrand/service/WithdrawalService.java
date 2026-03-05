package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.dto.WithdrawalApplyDTO;
import com.quickerrand.dto.WithdrawalAuditDTO;
import com.quickerrand.vo.WithdrawalRecordVO;

import java.util.List;

/**
 * 提现服务接口
 *
 * @author 周政
 * @date 2026-01-27
 */
public interface WithdrawalService {

    /**
     * 申请提现
     *
     * @param userId 用户ID
     * @param applyDTO 提现申请信息
     */
    void applyWithdrawal(Long userId, WithdrawalApplyDTO applyDTO);

    /**
     * 获取提现记录列表
     *
     * @param userId 用户ID
     * @return 提现记录列表
     */
    List<WithdrawalRecordVO> getWithdrawalRecords(Long userId);

    /**
     * 管理端分页查询提现记录
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param status   状态（可选）
     * @param keyword  关键词（用户ID或账户信息，可选）
     * @return 分页提现记录
     */
    Page<WithdrawalRecordVO> getAdminWithdrawalPage(Integer pageNum, Integer pageSize, Integer status, String keyword);

    /**
     * 审核提现申请
     *
     * @param auditDTO 审核参数
     */
    void auditWithdrawal(WithdrawalAuditDTO auditDTO);

    /**
     * 标记提现已到账
     *
     * @param id 提现记录ID
     */
    void markWithdrawalTransferred(Long id);

    /**
     * 删除提现记录
     *
     * @param ids 提现记录ID列表
     * @return 删除数量
     */
    int deleteWithdrawalRecords(List<Long> ids);

    /**
     * 删除单条提现记录
     *
     * @param id 提现记录ID
     */
    void deleteWithdrawalRecord(Long id);
}
