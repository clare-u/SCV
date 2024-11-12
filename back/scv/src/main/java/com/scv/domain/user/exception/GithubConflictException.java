package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class GithubConflictException extends ServiceException {

    private static final GithubConflictException INSTANCE = new GithubConflictException();

    private GithubConflictException() {
        super(ErrorCode.GITHUB_API_CONFLICT);
    }

    public static GithubConflictException getInstance() {
        return INSTANCE;
    }
}
