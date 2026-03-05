package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 *
 * @author 周政
 * @date 2026-01-26
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 物理删除用户
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM t_user WHERE id = #{userId}")
    int physicalDelete(@Param("userId") Long userId);

    /**
     * 批量物理删除用户
     *
     * @param ids 用户ID列表
     * @return 影响行数
     */
    @Delete("<script>" +
            "DELETE FROM t_user WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchPhysicalDelete(@Param("ids") java.util.List<Long> ids);
}
