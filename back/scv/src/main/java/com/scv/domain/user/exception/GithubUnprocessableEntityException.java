package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class GithubUnprocessableEntityException extends ServiceException {

    private static final GithubUnprocessableEntityException INSTANCE = new GithubUnprocessableEntityException();

    private GithubUnprocessableEntityException() {
        super(ErrorCode.GITHUB_API_UNPROCESSABLE_ENTITY);
    }

    public static GithubUnprocessableEntityException getInstance() {
        return INSTANCE;
    }
}
