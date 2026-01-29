package com.devpull.credentialmanagerservice.shared.error;

import java.time.OffsetDateTime;

public record ApiError(
        String code,
        String message,
        int status,
        String path,
        OffsetDateTime timestamp
) {
}
