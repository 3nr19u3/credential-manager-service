package com.devpull.credentialmanagerservice.application.credentials;

import com.devpull.credentialmanagerservice.api.v1.credentials.dto.CredentialApiMapper;
import com.devpull.credentialmanagerservice.domain.credential.Credential;
import com.devpull.credentialmanagerservice.domain.credential.CredentialId;
import com.devpull.credentialmanagerservice.domain.credential.CredentialStatus;
import com.devpull.credentialmanagerservice.domain.credential.CredentialType;
import com.devpull.credentialmanagerservice.domain.user.UserId;
import com.devpull.credentialmanagerservice.infrastructure.persistence.entity.CredentialEntity;
import com.devpull.credentialmanagerservice.infrastructure.persistence.mapper.CredentialEntityMapper;
import com.devpull.credentialmanagerservice.infrastructure.persistence.repository.CredentialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialServiceTest {

    @Mock
    CredentialRepository repo;
    @Mock
    CredentialEntityMapper entityMapper;
    @Mock
    CredentialApiMapper apiMapper;

    @InjectMocks
    CredentialService service;

    @Test
    void softDelete_whenApproved_returnsConflict() {
        long userId = 10L;
        long credId = 2L;

        CredentialEntity ce = new CredentialEntity();
        ce.setId(credId);
        ce.setUserId(userId);

        Credential domain = new Credential(
                new CredentialId(credId),
                new UserId(userId),
                CredentialType.EPA_608,
                "Issuer",
                "LIC",
                LocalDate.parse("2027-12-31"),
                CredentialStatus.APPROVED,
                null,
                OffsetDateTime.now()
        );

        when(repo.findByIdAndUser(credId, userId)).thenReturn(Mono.just(ce));
        when(entityMapper.toDomain(ce)).thenReturn(domain);

        StepVerifier.create(service.softDelete(userId, credId))
                .expectError(ResponseStatusException.class) // o tu DomainException si lo propagas
                .verify();

        verify(repo, never()).softDelete(anyLong(), anyLong());
    }

    @Test
    void adminUpdateStatus_invalidStatus_returnsBadRequest() {
        StepVerifier.create(service.adminUpdateStatus(1L, CredentialStatus.PENDING))
                .expectError(ResponseStatusException.class)
                .verify();
    }
}
