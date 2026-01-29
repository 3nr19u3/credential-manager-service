package com.devpull.credentialmanagerservice.domain.shared.error;

public enum ApiErrorCode {

    // ===== 400 =====
    BAD_REQUEST("BAD_REQUEST", "Bad request"),
    INVALID_CREDENTIAL_TYPE(
            "INVALID_CREDENTIAL_TYPE",
            "Invalid credential type. Allowed: HVAC_LICENSE, EPA_608, INSURANCE, STATE_LICENSE"
    ),
    INVALID_CREDENTIAL_STATUS(
            "INVALID_CREDENTIAL_STATUS",
            "Invalid credential status. Allowed: APPROVED, REJECTED"
    ),

    // ===== 401 =====
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid email or password"),

    // ===== 404 =====
    NOT_FOUND("NOT_FOUND", "Resource not found"),

    // ===== 409 =====
    CONFLICT("CONFLICT", "Conflict"),

    // ===== 422 =====
    DOMAIN_RULE_VIOLATION("DOMAIN_RULE_VIOLATION", "Business rule violation"),

    // ===== 500 =====
    INTERNAL_ERROR("INTERNAL_ERROR", "Unexpected error");

    private final String code;
    private final String defaultMessage;

    ApiErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String code() {
        return code;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
