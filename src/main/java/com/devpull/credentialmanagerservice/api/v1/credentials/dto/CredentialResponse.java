package com.devpull.credentialmanagerservice.api.v1.credentials.dto;

import com.devpull.credentialmanagerservice.domain.credential.CredentialStatus;
import com.devpull.credentialmanagerservice.domain.credential.CredentialType;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record CredentialResponse(
        Long id,
        CredentialType type,
        String issuer,
        String license_number,
        LocalDate expiry_date,
        CredentialStatus status,
        OffsetDateTime created_at
) {}
