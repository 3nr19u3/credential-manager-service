package com.devpull.credentialmanagerservice.domain.credential;

public record CredentialId(long value) {
    public CredentialId {
        if (value <= 0) throw new IllegalArgumentException("CredentialId must be positive");
    }
}
