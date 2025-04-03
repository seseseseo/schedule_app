package com.example.schedule.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "001_USER_NOT_FOUND","사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "002_DUPLICATED_EMAIL","이미 사용중인 이메일입니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "003_SCHEDULE_NOT_FOUND","스케쥴을 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "004_INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),
    INTERNAL_ERROR(HttpStatus.BAD_REQUEST,"005_INTERNAL_ERROR","서버 오류가 발생했습니다");

    private HttpStatus status;
    private final String code;
    private final String message;
    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
    public HttpStatus getStatus() {
        return status;
    }
    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
