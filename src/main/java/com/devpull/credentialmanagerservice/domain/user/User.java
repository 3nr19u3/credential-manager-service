package com.devpull.credentialmanagerservice.domain.user;

import java.time.OffsetDateTime;

public record User(
        UserId id,
        Email email,
        String passwordHash,
        OffsetDateTime createdAt
) {
    public User {
        if (email == null) throw new IllegalArgumentException("email required");
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("passwordHash required");
    }

    public User withoutPasswordHash() {
        return new User(id, email, "********", createdAt);
    }
}
