package com.devpull.credentialmanagerservice.domain.shared;

import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;

public abstract class DomainException extends RuntimeException {

    private final ApiErrorCode errorCode;

    protected DomainException(ApiErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }

    public ApiErrorCode errorCode() {
        return errorCode;
    }

}
