package com.devpull.credentialmanagerservice.application.auth;

import com.devpull.credentialmanagerservice.application.shared.AppException;
import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;

public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException() {
        super(ApiErrorCode.INVALID_CREDENTIALS);
    }
}
