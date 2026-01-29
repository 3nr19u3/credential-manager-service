package com.devpull.credentialmanagerservice.api.v1.admin.dto;

import com.devpull.credentialmanagerservice.domain.credential.CredentialStatus;

public record UpdateStatusRequest(CredentialStatus status) {
}
