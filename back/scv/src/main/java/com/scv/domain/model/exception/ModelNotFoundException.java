package com.scv.domain.model.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class ModelNotFoundException extends ServiceException {
    public ModelNotFoundException() {
        super(ErrorCode.MODEL_NOT_FOUND);
    }
}
