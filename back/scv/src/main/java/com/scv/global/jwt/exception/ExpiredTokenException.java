package com.scv.global.jwt.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class ExpiredTokenException extends ServiceException {

    private static final ExpiredTokenException INSTANCE = new ExpiredTokenException();

    private ExpiredTokenException() {
        super(ErrorCode.EXPIRED_TOKEN);
    }

    public static ExpiredTokenException getInstance() {
        return INSTANCE;
    }
}
