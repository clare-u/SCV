package com.scv.global.jwt.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class InvalidTokenException extends ServiceException {

    private static final InvalidTokenException INSTANCE = new InvalidTokenException();

    private InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }

    public static InvalidTokenException getInstance() {
        return INSTANCE;
    }
}
