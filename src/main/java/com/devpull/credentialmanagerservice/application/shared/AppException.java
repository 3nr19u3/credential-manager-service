package com.devpull.credentialmanagerservice.application.shared;

import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;

public abstract class AppException extends RuntimeException {


    private final ApiErrorCode errorCode;

    protected AppException(ApiErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }

    protected AppException(ApiErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public ApiErrorCode errorCode() {
        return errorCode;
    }

}
