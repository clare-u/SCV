package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class GithubNotFoundException extends ServiceException {

    private static final GithubNotFoundException INSTANCE = new GithubNotFoundException();

    private GithubNotFoundException() {
        super(ErrorCode.GITHUB_API_NOT_FOUND);
    }

    public static GithubNotFoundException getInstance() {
        return INSTANCE;
    }
}
