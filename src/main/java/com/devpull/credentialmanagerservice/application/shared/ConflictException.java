package com.devpull.credentialmanagerservice.application.shared;

import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;

public class ConflictException extends AppException {
    public ConflictException(String code, String message) {
        super(ApiErrorCode.CONFLICT, message);
    }
}
