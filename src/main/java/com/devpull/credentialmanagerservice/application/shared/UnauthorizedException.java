package com.devpull.credentialmanagerservice.application.shared;

import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;

public class UnauthorizedException extends AppException {
    public UnauthorizedException() {
        super(ApiErrorCode.UNAUTHORIZED);
    }
}
