package com.devpull.credentialmanagerservice.application.auth;

import com.devpull.credentialmanagerservice.api.v1.auth.dto.AuthResponse;
import com.devpull.credentialmanagerservice.api.v1.auth.dto.LoginRequest;
import com.devpull.credentialmanagerservice.infrastructure.jwt.JwtService;
import com.devpull.credentialmanagerservice.infrastructure.persistence.entity.UserEntity;
import com.devpull.credentialmanagerservice.infrastructure.persistence.mapper.UserEntityMapper;
import com.devpull.credentialmanagerservice.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository users;

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;

    @Mock
    UserEntityMapper userMapper;

    @InjectMocks
    AuthService authService;

    @Test
    void login_invalidEmail_returnsInvalidCredentials() {
        when(users.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(new LoginRequest("x@y.com", "pass")))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }

    @Test
    void login_invalidPassword_returnsInvalidCredentials() {
        UserEntity ue = new UserEntity();
        ue.setId(1L);
        ue.setEmail("x@y.com");
        ue.setPasswordHash("HASH");

        when(users.findByEmail("x@y.com")).thenReturn(Mono.just(ue));
        when(passwordEncoder.matches(eq("bad"), eq("HASH"))).thenReturn(false);

        StepVerifier.create(authService.login(new LoginRequest("x@y.com", "bad")))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }

    @Test
    void login_ok_returnsToken() {
        UserEntity ue = new UserEntity();
        ue.setId(1L);
        ue.setEmail("x@y.com");
        ue.setPasswordHash("HASH");

        when(users.findByEmail("x@y.com")).thenReturn(Mono.just(ue));
        when(passwordEncoder.matches(eq("good"), eq("HASH"))).thenReturn(true);

        var domainUser = userMapper.toDomain(ue);
        when(userMapper.toDomain(ue)).thenReturn(domainUser);
        when(jwtService.createToken(domainUser)).thenReturn("jwt.token");

        StepVerifier.create(authService.login(new LoginRequest("x@y.com", "good")))
                .expectNextMatches((AuthResponse ar) -> ar.accessToken().equals("jwt.token") && ar.tokenType().equals("Bearer"))
                .verifyComplete();
    }

}
