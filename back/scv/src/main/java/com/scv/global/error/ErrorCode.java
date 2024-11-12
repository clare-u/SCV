package com.scv.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    GITHUB_API_BAD_REQUEST(400, "GITHUB_API_BAD_REQUEST", "Bad Request"),
    GITHUB_API_UNAUTHORIZED(401, "GITHUB_API_UNAUTHORIZED", "Requires authentication"),
    GITHUB_API_FORBIDDEN(403, "GITHUB_API_FORBIDDEN", "Forbidden"),
    GITHUB_API_NOT_FOUND(404, "GITHUB_API_NOT_FOUND", "Resource not found"),
    GITHUB_API_CONFLICT(409, "GITHUB_API_CONFLICT", "Conflict"),
    GITHUB_API_UNPROCESSABLE_ENTITY(422, "GITHUB_API_UNPROCESSABLE_ENTITY", "Validation failed, or the endpoint has been spammed."),
    INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(403, "EXPIRED_TOKEN", "만료된 토큰입니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),

    RESULT_NOT_FOUND(404, "RESULT_NOT_FOUND", "결과를 찾을 수 없습니다."),
    DATA_NOT_FOUND(404, "DATA_NOT_FOUND", "데이터를 찾을 수 없습니다."),
    MODEL_VERSION_NOT_FOUND(404, "MODEL_VERSION_NOT_FOUND", "버전을 찾을 수 없습니다."),
    MODEL_NOT_FOUND(404, "MODEL_NOT_FOUND", "모델을 찾을 수 없습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}