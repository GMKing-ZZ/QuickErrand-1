package com.quickerrand.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户详情VO（管理员查看）
 *
 * @author 周政
 * @date 2026-03-08
 */
@Data
public class UserDetailVO {

    private Long id;

    private String username;

    private String phone;

    private String nickname;

    private String avatar;

    private Integer gender;

    private String genderText;

    private String birthday;

    private Integer userType;

    private String userTypeText;

    private Integer status;

    private String statusText;

    private BigDecimal balance;

    private Integer pickupCodeEnabled;

    private String openid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private RunnerInfoVO runnerInfo;

    @Data
    public static class RunnerInfoVO {

        private Long id;

        private String realName;

        private String idCard;

        private String idCardFront;

        private String idCardBack;

        private Integer certStatus;

        private String certStatusText;

        private String rejectReason;

        private Integer creditLevel;

        private Integer totalOrders;

        private BigDecimal goodRate;

        private String serviceTime;

        private Integer serviceRange;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime certTime;
    }
}
