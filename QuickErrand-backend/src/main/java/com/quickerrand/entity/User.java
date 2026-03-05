package com.quickerrand.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户实体类
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    /**
     * 逻辑删除标记（覆盖 BaseEntity 的 deleted 字段，映射到 is_deleted）
     * 注意：User 表使用 is_deleted 字段，需要特殊映射
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别（0未知1男2女）
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 用户类型（1普通用户2跑腿员3管理员）
     */
    private Integer userType;

    /**
     * 状态（0禁用1正常）
     */
    private Integer status;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 是否默认开启收货码（0关闭1开启）
     */
    private Integer pickupCodeEnabled;

}
