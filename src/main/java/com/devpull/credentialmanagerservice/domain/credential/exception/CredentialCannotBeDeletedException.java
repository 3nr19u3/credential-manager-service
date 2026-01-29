package com.devpull.credentialmanagerservice.domain.credential.exception;

import com.devpull.credentialmanagerservice.domain.shared.DomainException;
import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;

public class CredentialCannotBeDeletedException extends DomainException {
    public CredentialCannotBeDeletedException() {
        super(ApiErrorCode.DOMAIN_RULE_VIOLATION);
    }
}
