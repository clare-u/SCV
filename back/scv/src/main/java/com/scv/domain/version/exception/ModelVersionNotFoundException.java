package com.scv.domain.version.exception;

import com.scv.global.error.ErrorCode;
import com.scv.global.error.ServiceException;

public class ModelVersionNotFoundException extends ServiceException {
    public ModelVersionNotFoundException() {
        super(ErrorCode.MODEL_VERSION_NOT_FOUND);
    }
}
