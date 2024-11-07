package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class RepositoryNotFoundException extends ServiceException {

    private static final RepositoryNotFoundException INSTANCE = new RepositoryNotFoundException();

    private RepositoryNotFoundException() {
        super(ErrorCode.REPO_NOT_FOUND);
    }

    public static RepositoryNotFoundException getInstance() {
        return INSTANCE;
    }
}
