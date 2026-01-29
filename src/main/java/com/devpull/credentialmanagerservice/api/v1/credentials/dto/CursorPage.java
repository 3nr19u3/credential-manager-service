package com.devpull.credentialmanagerservice.api.v1.credentials.dto;

import java.util.List;

public record CursorPage<T>(List<T> items, String nextCursor) {
}
