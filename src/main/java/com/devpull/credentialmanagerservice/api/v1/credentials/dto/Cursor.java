package com.devpull.credentialmanagerservice.api.v1.credentials.dto;

import java.time.OffsetDateTime;

public record Cursor(OffsetDateTime createdAt, long id) {}

