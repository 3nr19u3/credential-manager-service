package com.devpull.credentialmanagerservice.shared.error;

import com.devpull.credentialmanagerservice.application.shared.AppException;
import com.devpull.credentialmanagerservice.application.shared.BadRequestException;
import com.devpull.credentialmanagerservice.domain.shared.DomainException;
import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;


import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

@Component
@Order(-2)// sure be able before of default handler
public class GlobalErrorHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        AppException mapped = mapEnumErrors(ex);
        if (mapped != null) {
            return write(exchange, mapped);
        }

        ApiErrorCode errorCode = mapErrorCode(ex);
        HttpStatus status = mapStatus(errorCode);

        ApiError body = new ApiError(
                errorCode.code(),
                ex.getMessage() != null ? ex.getMessage() : errorCode.defaultMessage(),
                status.value(),
                exchange.getRequest().getPath().value(),
                OffsetDateTime.now()
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"code\":\"INTERNAL_ERROR\",\"message\":\"Serialization error\"}")
                    .getBytes(StandardCharsets.UTF_8);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    private Mono<Void> write(ServerWebExchange exchange, AppException ex) {

        ApiErrorCode errorCode = mapErrorCode(ex);
        HttpStatus status = mapStatus(errorCode);

        ApiError body = new ApiError(
                errorCode.code(),
                ex.getMessage() != null ? ex.getMessage() : errorCode.defaultMessage(),
                status.value(),
                exchange.getRequest().getPath().value(),
                OffsetDateTime.now()
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = "{\"code\":\"INTERNAL_ERROR\",\"message\":\"Serialization error\",\"status\":500}"
                    .getBytes(StandardCharsets.UTF_8);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }


    private static HttpStatus mapStatus(ApiErrorCode code) {
        return switch (code) {
            case BAD_REQUEST,
                 INVALID_CREDENTIAL_TYPE,
                 INVALID_CREDENTIAL_STATUS -> HttpStatus.BAD_REQUEST;

            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case DOMAIN_RULE_VIOLATION -> HttpStatus.UNPROCESSABLE_ENTITY;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }


    private static AppException mapEnumErrors(Throwable ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {

            Class<?> targetType = ife.getTargetType();

            if (targetType != null && targetType.isEnum()
                    && targetType.getSimpleName().equals("CredentialType")) {

                return new BadRequestException(ApiErrorCode.INVALID_CREDENTIAL_TYPE);
            }
        }
        return null;
    }

    private static ApiErrorCode mapErrorCode(Throwable ex) {

        if (ex instanceof AppException ae) return ae.errorCode();
        if (ex instanceof DomainException de) return de.errorCode();

        InvalidFormatException ife = findCause(ex, InvalidFormatException.class);

        if (ife != null) {
            Class<?> target = ife.getTargetType();

            if (target != null && target.equals(com.devpull.credentialmanagerservice.domain.credential.CredentialStatus.class)) {
                return ApiErrorCode.INVALID_CREDENTIAL_STATUS;
            }
            if (target != null && target.equals(com.devpull.credentialmanagerservice.domain.credential.CredentialType.class)) {
                return ApiErrorCode.INVALID_CREDENTIAL_TYPE;
            }

            if (mentionsJsonField(ife, "status")) return ApiErrorCode.INVALID_CREDENTIAL_STATUS;
            if (mentionsJsonField(ife, "type")) return ApiErrorCode.INVALID_CREDENTIAL_TYPE;

            return ApiErrorCode.BAD_REQUEST;
        }

        if (ex instanceof org.springframework.web.server.ServerWebInputException) return ApiErrorCode.BAD_REQUEST;
        if (ex instanceof org.springframework.core.codec.DecodingException) return ApiErrorCode.BAD_REQUEST;

        if (ex instanceof IllegalArgumentException) return ApiErrorCode.BAD_REQUEST;

        return ApiErrorCode.INTERNAL_ERROR;
    }

    private static <T extends Throwable> T findCause(Throwable ex, Class<T> type) {
        Throwable cur = ex;
        while (cur != null) {
            if (type.isInstance(cur)) return type.cast(cur);
            cur = cur.getCause();
        }
        return null;
    }

    private static boolean mentionsJsonField(InvalidFormatException ife, String field) {
        return ife.getPath() != null &&
                ife.getPath().stream().anyMatch(ref -> field.equals(ref.getFieldName()));
    }

}
