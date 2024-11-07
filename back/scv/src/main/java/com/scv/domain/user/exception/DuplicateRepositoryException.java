package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class DuplicateRepositoryException extends ServiceException {

    private static final DuplicateRepositoryException INSTANCE = new DuplicateRepositoryException();

    private DuplicateRepositoryException() {
        super(ErrorCode.DUPLICATE_REPO_NAME);
    }

    public static DuplicateRepositoryException getInstance() {
        return INSTANCE;
    }
}
