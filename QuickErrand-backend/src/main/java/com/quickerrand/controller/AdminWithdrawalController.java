package com.quickerrand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.dto.WithdrawalAuditDTO;
import com.quickerrand.service.WithdrawalService;
import com.quickerrand.vo.WithdrawalRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 管理员提现审核管理控制器
 *
 * @author 周政
 * @date 2026-02-13
 */
@Slf4j
@Api(tags = "管理员提现审核管理接口")
@RestController
@RequestMapping("/admin/withdrawal")
public class AdminWithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @ApiOperation("获取提现申请列表")
    @GetMapping("/list")
    public Result<Page<WithdrawalRecordVO>> getWithdrawalList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {

        Page<WithdrawalRecordVO> page = withdrawalService.getAdminWithdrawalPage(pageNum, pageSize, status, keyword);
        return Result.success(page);
    }

    @ApiOperation("审核提现申请")
    @PostMapping("/audit")
    public Result<Void> auditWithdrawal(@Validated @RequestBody WithdrawalAuditDTO auditDTO) {
        withdrawalService.auditWithdrawal(auditDTO);
        return Result.success();
    }

    @ApiOperation("标记提现已到账")
    @PostMapping("/transfer/{id}")
    public Result<Void> markTransferred(@PathVariable Long id) {
        withdrawalService.markWithdrawalTransferred(id);
        return Result.success();
    }

    @ApiOperation("删除提现记录")
    @PostMapping("/delete")
    public Result<Integer> deleteWithdrawal(@RequestBody DeleteRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return Result.success(0);
        }
        int deleted = withdrawalService.deleteWithdrawalRecords(request.getIds());
        return Result.success(deleted);
    }

    @ApiOperation("删除单条提现记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteWithdrawalById(@PathVariable Long id) {
        withdrawalService.deleteWithdrawalRecord(id);
        return Result.success();
    }

    @Data
    public static class DeleteRequest {
        @NotEmpty(message = "待删除记录ID不能为空")
        private List<Long> ids;
    }
}

