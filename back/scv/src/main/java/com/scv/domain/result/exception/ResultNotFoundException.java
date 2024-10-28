package com.scv.domain.result.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class ResultNotFoundException extends ServiceException {
    public ResultNotFoundException() {
        super(ErrorCode.RESULT_NOT_FOUND);
    }
}
