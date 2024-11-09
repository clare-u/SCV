package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class GithubBadRequestException extends ServiceException {

    private static final GithubBadRequestException INSTANCE = new GithubBadRequestException();

    private GithubBadRequestException() {
        super(ErrorCode.GITHUB_API_BAD_REQUEST);
    }

    public static GithubBadRequestException getInstance() {
        return INSTANCE;
    }
}
