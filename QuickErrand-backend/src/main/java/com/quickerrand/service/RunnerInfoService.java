package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.quickerrand.dto.RunnerAuthApplyDTO;
import com.quickerrand.dto.RunnerAuthApprovalDTO;
import com.quickerrand.dto.UpdateRunnerServiceDTO;
import com.quickerrand.entity.RunnerInfo;
import com.quickerrand.vo.RunnerAuthListVO;
import com.quickerrand.vo.RunnerInfoVO;
import com.quickerrand.vo.RunnerPublicInfoVO;

/**
 * 跑腿员信息Service接口
 *
 * @author 周政
 * @date 2026-01-26
 */
public interface RunnerInfoService extends IService<RunnerInfo> {

    /**
     * 申请跑腿员认证
     *
     * @param userId 用户ID
     * @param applyDTO 申请信息
     */
    void applyAuth(Long userId, RunnerAuthApplyDTO applyDTO);

    /**
     * 审核跑腿员认证
     *
     * @param approvalDTO 审核信息
     */
    void approveAuth(RunnerAuthApprovalDTO approvalDTO);

    /**
     * 获取跑腿员认证信息
     *
     * @param userId 用户ID
     * @return 跑腿员信息
     */
    RunnerInfoVO getAuthInfo(Long userId);

    /**
     * 管理员获取认证申请列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param certStatus 认证状态
     * @param keyword 搜索关键词
     * @return 认证申请列表
     */
    Page<RunnerAuthListVO> getAuthApplicationList(Integer pageNum, Integer pageSize, Integer certStatus, String keyword);

    /**
     * 更新跑腿员服务设置
     *
     * @param userId 用户ID
     * @param updateDTO 服务设置信息
     */
    void updateServiceSettings(Long userId, UpdateRunnerServiceDTO updateDTO);

    /**
     * 获取跑腿员公开信息（用户端查看）
     *
     * @param runnerUserId 跑腿员用户ID
     * @return 跑腿员公开信息
     */
    RunnerPublicInfoVO getRunnerPublicInfo(Long runnerUserId);

    /**
     * 删除认证申请记录
     *
     * @param runnerInfoId 跑腿员信息ID
     */
    void deleteAuthApplication(Long runnerInfoId);

    /**
     * 批量删除认证申请记录
     *
     * @param runnerInfoIds 跑腿员信息ID列表
     */
    void batchDeleteAuthApplications(java.util.List<Long> runnerInfoIds);

}
