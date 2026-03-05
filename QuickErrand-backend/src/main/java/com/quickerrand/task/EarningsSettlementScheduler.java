package com.quickerrand.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.quickerrand.entity.EarningsRecord;
import com.quickerrand.mapper.EarningsRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 收益结算定时任务：T+3 天自动结算
 *
 * 跑腿员订单完成后会生成一条 type=1、status=1 的收益记录（待结算），
 * 此任务会在 T+3 天后自动将状态更新为 2（已结算），用于收益统计与提现。
 *
 * @author 周政（扩展：自动结算）
 * @date 2026-02-13
 */
@Slf4j
@Component
public class EarningsSettlementScheduler {

    @Autowired
    private EarningsRecordMapper earningsRecordMapper;

    /**
     * 每天凌晨 3 点执行一次 T+3 自动结算
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void autoSettlePendingEarnings() {
        // 截止时间 = 当前时间往前推 3 天
        LocalDateTime cutoff = LocalDateTime.now().minusDays(3);

        LambdaUpdateWrapper<EarningsRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.ne(EarningsRecord::getType, 3)   // 排除提现记录
               .eq(EarningsRecord::getStatus, 1) // 仅结算待结算记录
               .le(EarningsRecord::getCreateTime, cutoff)
               .set(EarningsRecord::getStatus, 2);

        int updated = earningsRecordMapper.update(null, wrapper);
        if (updated > 0) {
            log.info("T+3 自动结算完成，本次更新收益记录数：{}", updated);
        }
    }
}

