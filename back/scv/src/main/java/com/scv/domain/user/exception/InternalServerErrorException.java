package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class InternalServerErrorException extends ServiceException {

    private static final InternalServerErrorException INSTANCE = new InternalServerErrorException();

    private InternalServerErrorException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public static InternalServerErrorException getInstance() {
        return INSTANCE;
    }
}
