package com.devpull.credentialmanagerservice.application.auth;

import com.devpull.credentialmanagerservice.application.shared.BadRequestException;
import com.devpull.credentialmanagerservice.application.shared.ConflictException;
import com.devpull.credentialmanagerservice.domain.shared.error.ApiErrorCode;
import com.devpull.credentialmanagerservice.domain.user.Email;
import com.devpull.credentialmanagerservice.infrastructure.jwt.JwtService;
import com.devpull.credentialmanagerservice.api.v1.auth.dto.AuthResponse;
import com.devpull.credentialmanagerservice.api.v1.auth.dto.LoginRequest;
import com.devpull.credentialmanagerservice.api.v1.auth.dto.RegisterRequest;
import com.devpull.credentialmanagerservice.infrastructure.persistence.entity.UserEntity;
import com.devpull.credentialmanagerservice.infrastructure.persistence.mapper.UserEntityMapper;
import com.devpull.credentialmanagerservice.infrastructure.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserEntityMapper userMapper;


    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService, UserEntityMapper userMapper) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    public Mono<Void> register(RegisterRequest req) {
        Email email = Email.of(req.email());
        return users.findByEmail(email.value())
                .flatMap(u -> Mono.<Void>error(new ConflictException("EMAIL_EXISTS", "Email already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    UserEntity e = new UserEntity();
                    e.setEmail(email.value());
                    e.setPasswordHash(passwordEncoder.encode(req.password()));
                    return users.save(e).then();
                }));
    }

    public Mono<AuthResponse> login(LoginRequest req) {
    Email email;
        try {
           email = Email.of(req.email());
        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidCredentialsException());
        }

        return users.findByEmail(email.value())
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(e -> {
                    if (!passwordEncoder.matches(req.password(), e.getPasswordHash())) {
                        return Mono.error(new InvalidCredentialsException());
                    }
                    var domainUser = userMapper.toDomain(e);
                    return Mono.just(new AuthResponse(jwtService.createToken(domainUser), "Bearer"));
                });
    }
}
