package com.devpull.credentialmanagerservice.api.v1.credentials;

import com.devpull.credentialmanagerservice.api.v1.credentials.dto.CredentialCreateRequest;
import com.devpull.credentialmanagerservice.api.v1.credentials.dto.CredentialResponse;
import com.devpull.credentialmanagerservice.api.v1.credentials.dto.CursorPage;
import com.devpull.credentialmanagerservice.application.credentials.CredentialService;
import com.devpull.credentialmanagerservice.domain.credential.CredentialStatus;
import com.devpull.credentialmanagerservice.domain.credential.CredentialType;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/credentials")
public class CredentialsController {

    private final CredentialService service;

    public CredentialsController(CredentialService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CredentialResponse> create(@RequestBody CredentialCreateRequest req, Authentication auth) {
        long userId = requireUserId(auth);
        return service.create(userId, req);
    }

    @GetMapping
    public Mono<CursorPage<CredentialResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) CredentialType type,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String cursor,
            Authentication auth
    ) {
        long userId = requireUserId(auth);
        CredentialStatus st = (status == null || status.isBlank()) ? null : CredentialStatus.valueOf(status);
        return service.list(userId, type, st, limit, cursor);
    }

    @GetMapping("/{id}")
    public Mono<CredentialResponse> getOne(@PathVariable long id, Authentication auth) {
        long userId = requireUserId(auth);
        return service.getOne(userId, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable long id, Authentication auth) {
        long userId = requireUserId(auth);
        return service.softDelete(userId, id);
    }

    static long requireUserId(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT");
        }
        return Long.parseLong(jwt.getSubject());
    }
}
