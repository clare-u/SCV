package com.scv.global.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int httpStatus;
    private final String code;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}