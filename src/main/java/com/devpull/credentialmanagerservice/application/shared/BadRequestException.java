package com.devpull.credentialmanagerservice.application.shared;

import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;

public class BadRequestException extends AppException {
    public BadRequestException(ApiErrorCode code) {
        super(code);
    }
}
