package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class GithubFileNotFoundException extends ServiceException {

    private static final GithubFileNotFoundException INSTANCE = new GithubFileNotFoundException();

    private GithubFileNotFoundException() {
        super(ErrorCode.GITHUB_API_FILE_NOT_FOUND);
    }

    public static GithubFileNotFoundException getInstance() {
        return INSTANCE;
    }
}
