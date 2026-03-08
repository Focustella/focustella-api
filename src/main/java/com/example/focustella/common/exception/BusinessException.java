package com.example.focustella.common.exception;

import com.example.focustella.common.exception.code.ErrorCodeSpec;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCodeSpec errorCode;

    public BusinessException(ErrorCodeSpec errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCodeSpec errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
