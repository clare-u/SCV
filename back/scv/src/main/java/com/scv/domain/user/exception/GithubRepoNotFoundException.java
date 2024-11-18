package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class GithubRepoNotFoundException extends ServiceException {

    private static final GithubRepoNotFoundException INSTANCE = new GithubRepoNotFoundException();

    private GithubRepoNotFoundException() {
        super(ErrorCode.GITHUB_API_REPO_NOT_FOUND);
    }

    public static GithubRepoNotFoundException getInstance() {
        return INSTANCE;
    }
}
