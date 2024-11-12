package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class GithubUnauthorizedException extends ServiceException {

    private static final GithubUnauthorizedException INSTANCE = new GithubUnauthorizedException();

    private GithubUnauthorizedException() {
        super(ErrorCode.GITHUB_API_UNAUTHORIZED);
    }

    public static GithubUnauthorizedException getInstance() {
        return INSTANCE;
    }
}
