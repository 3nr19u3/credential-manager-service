package com.devpull.credentialmanagerservice.application.shared;

import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;

public class NotFoundException extends AppException {
    public NotFoundException(String resource, Object id) {
        super(ApiErrorCode.NOT_FOUND, resource + " not found: " + id);
    }
}
