package com.quickerrand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BlacklistedRunnerVO {
    
    private Long id;
    
    private Long runnerId;
    
    private String runnerName;
    
    private String runnerAvatar;
    
    private String runnerPhone;
    
    private Integer creditLevel;
    
    private Integer totalOrders;
    
    private BigDecimal goodRate;
    
    private String reason;
    
    private LocalDateTime createTime;
}
