package com.devpull.credentialmanagerservice.domain.credential;

import com.devpull.credentialmanagerservice.domain.credential.exception.CredentialCannotBeDeletedException;
import com.devpull.credentialmanagerservice.domain.user.UserId;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record Credential(
        CredentialId id,
        UserId userId,
        CredentialType type,
        String issuer,
        String licenseNumber,
        LocalDate expiryDate,
        CredentialStatus status,
        OffsetDateTime deletedAt,
        OffsetDateTime createdAt
) {

    public Credential {
        if (userId == null) throw new IllegalArgumentException("userId required");
        if (type == null) throw new IllegalArgumentException("type required");
        if (issuer == null || issuer.isBlank()) throw new IllegalArgumentException("issuer required");
        if (licenseNumber == null || licenseNumber.isBlank()) throw new IllegalArgumentException("licenseNumber required");
        if (expiryDate == null) throw new IllegalArgumentException("expiryDate required");
        if (status == null) throw new IllegalArgumentException("status required");
    }

    /** soft-delete only if PENDING or REJECTED */
    public boolean canSoftDelete() {
        return status == CredentialStatus.PENDING || status == CredentialStatus.REJECTED;
    }


    public Credential softDeleted(OffsetDateTime when) {
        if (!canSoftDelete()) throw new CredentialCannotBeDeletedException();
        return new Credential(id, userId, type, issuer, licenseNumber, expiryDate, status, when, createdAt);
    }

}
