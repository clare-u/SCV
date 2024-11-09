package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class GithubForbiddenException extends ServiceException {

    private static final GithubForbiddenException INSTANCE = new GithubForbiddenException();

    private GithubForbiddenException() {
        super(ErrorCode.GITHUB_API_FORBIDDEN);
    }

    public static GithubForbiddenException getInstance() {
        return INSTANCE;
    }
}
