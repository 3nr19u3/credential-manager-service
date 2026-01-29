package com.devpull.credentialmanagerservice.api.v1.auth;

import com.devpull.credentialmanagerservice.application.auth.AuthService;
import com.devpull.credentialmanagerservice.api.v1.auth.dto.AuthResponse;
import com.devpull.credentialmanagerservice.api.v1.auth.dto.LoginRequest;
import com.devpull.credentialmanagerservice.api.v1.auth.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> register(@RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

}
