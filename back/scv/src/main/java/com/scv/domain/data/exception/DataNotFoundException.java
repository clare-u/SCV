package com.scv.domain.data.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class DataNotFoundException extends ServiceException {
    public DataNotFoundException() {
        super(ErrorCode.DATA_NOT_FOUND);
    }
}
