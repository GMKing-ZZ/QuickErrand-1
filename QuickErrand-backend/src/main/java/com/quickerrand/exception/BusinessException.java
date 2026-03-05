package com.quickerrand.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 *
 * @author 周政
 * @date 2026-01-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}
