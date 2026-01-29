package com.devpull.credentialmanagerservice.api.v1.credentials.dto;

import com.devpull.credentialmanagerservice.domain.credential.CredentialType;

import java.time.LocalDate;

public record CredentialCreateRequest(
        CredentialType type,
        String issuer,
        String license_number,
        LocalDate expiry_date
) {}
