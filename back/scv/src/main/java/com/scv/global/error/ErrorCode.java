package com.scv.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
    INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(403, "EXPIRED_TOKEN", "만료된 토큰입니다."),
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    RESULT_NOT_FOUND(404, "RESULT_NOT_FOUND", "결과를 찾을 수 없습니다."),
    MODEL_VERSION_NOT_FOUND(404, "MODEL_VERSION_NOT_FOUND", "버전을 찾을 수 없습니다."),
    MODEL_NOT_FOUND(404, "MODEL_NOT_FOUND", "모델을 찾을 수 없습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}