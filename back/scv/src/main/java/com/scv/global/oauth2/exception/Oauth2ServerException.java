package com.scv.global.oauth2.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class Oauth2ServerException extends ServiceException {

    private static final Oauth2ServerException INSTANCE = new Oauth2ServerException();

    private Oauth2ServerException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public static Oauth2ServerException getInstance() {
        return INSTANCE;
    }
}
