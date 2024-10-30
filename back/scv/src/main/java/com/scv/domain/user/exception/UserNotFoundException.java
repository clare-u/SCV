package com.scv.domain.user.exception;

public class UserNotFoundException extends RuntimeException {

    private static final UserNotFoundException INSTANCE = new UserNotFoundException("해당 유저를 찾지 못했습니다.");

    private UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException getInstance() {
        return INSTANCE;
    }
}
