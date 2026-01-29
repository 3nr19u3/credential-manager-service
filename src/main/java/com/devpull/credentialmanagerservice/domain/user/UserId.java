package com.devpull.credentialmanagerservice.domain.user;

public record UserId(long value) {
    public UserId {
        if (value <= 0) throw new IllegalArgumentException("UserId must be positive");
    }
}
