package com.devpull.credentialmanagerservice.infrastructure.util;

import com.devpull.credentialmanagerservice.api.v1.credentials.dto.Cursor;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

public final class CursorCodec {

    private CursorCodec() {}

    public static String encode(OffsetDateTime createdAt, long id) {
        String raw = createdAt.toString() + "|" + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Optional<Cursor> decode(@Nullable String cursor) {
        if (cursor == null || cursor.isBlank()) return Optional.empty();
        try {
            String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = raw.split("\\|");
            return Optional.of(new Cursor(OffsetDateTime.parse(parts[0]), Long.parseLong(parts[1])));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid cursor");
        }
    }

}
