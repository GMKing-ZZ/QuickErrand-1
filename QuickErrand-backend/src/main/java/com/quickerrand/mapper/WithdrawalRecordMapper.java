package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.WithdrawalRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现记录Mapper
 *
 * @author 周政
 * @date 2026-01-27
 */
@Mapper
public interface WithdrawalRecordMapper extends BaseMapper<WithdrawalRecord> {
}
