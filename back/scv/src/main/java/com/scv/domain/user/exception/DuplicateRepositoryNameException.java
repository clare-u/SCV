package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class DuplicateRepositoryNameException extends ServiceException {

    private static final DuplicateRepositoryNameException INSTANCE = new DuplicateRepositoryNameException();

    private DuplicateRepositoryNameException() {
        super(ErrorCode.DUPLICATE_REPO_NAME);
    }

    public static DuplicateRepositoryNameException getInstance() {
        return INSTANCE;
    }
}
