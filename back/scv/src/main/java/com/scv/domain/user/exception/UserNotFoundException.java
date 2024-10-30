package com.scv.domain.user.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class UserNotFoundException extends ServiceException {

    private static final UserNotFoundException INSTANCE = new UserNotFoundException();

    private UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public static UserNotFoundException getInstance() {
        return INSTANCE;
    }
}
