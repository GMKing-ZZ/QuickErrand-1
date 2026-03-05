package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告Mapper接口
 *
 * @author 周政
 * @date 2026-01-27
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
