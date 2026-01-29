package com.devpull.credentialmanagerservice.domain.user;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern BASIC = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null) throw new IllegalArgumentException("Email is required");
        String normalized = normalize(value);
        if (!BASIC.matcher(normalized).matches()) throw new IllegalArgumentException("Invalid email");
        value = normalized;
    }

    public static Email of(String raw) {
        return new Email(raw);
    }

    private static String normalize(String raw) {
        return raw.trim().toLowerCase();
    }
}
