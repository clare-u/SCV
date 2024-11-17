package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class InternalServerException extends ServiceException {

    private static final InternalServerException INSTANCE = new InternalServerException();

    private InternalServerException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public static InternalServerException getInstance() {
        return INSTANCE;
    }
}
