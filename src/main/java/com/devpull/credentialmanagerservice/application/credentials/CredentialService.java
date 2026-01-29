package com.devpull.credentialmanagerservice.application.credentials;

import com.devpull.credentialmanagerservice.api.v1.credentials.dto.CredentialApiMapper;
import com.devpull.credentialmanagerservice.api.v1.credentials.dto.CredentialCreateRequest;
import com.devpull.credentialmanagerservice.api.v1.credentials.dto.CredentialResponse;
import com.devpull.credentialmanagerservice.api.v1.credentials.dto.CursorPage;
import com.devpull.credentialmanagerservice.application.shared.NotFoundException;
import com.devpull.credentialmanagerservice.domain.credential.Credential;
import com.devpull.credentialmanagerservice.domain.credential.CredentialStatus;
import com.devpull.credentialmanagerservice.domain.credential.CredentialType;
import com.devpull.credentialmanagerservice.domain.user.UserId;
import com.devpull.credentialmanagerservice.infrastructure.persistence.mapper.CredentialEntityMapper;
import com.devpull.credentialmanagerservice.infrastructure.persistence.repository.CredentialRepository;
import com.devpull.credentialmanagerservice.infrastructure.util.CursorCodec;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CredentialService {


    private final CredentialRepository repo;
    private final CredentialEntityMapper entityMapper;
    private final CredentialApiMapper apiMapper;

    public CredentialService(CredentialRepository repo, CredentialEntityMapper entityMapper, CredentialApiMapper apiMapper) {
        this.repo = repo;
        this.entityMapper = entityMapper;
        this.apiMapper = apiMapper;
    }

    public Mono<CredentialResponse> create(long userId, CredentialCreateRequest req) {
        var domain = new Credential(
                null,
                new UserId(userId),
                req.type(),
                req.issuer(),
                req.license_number(),
                req.expiry_date(),
                CredentialStatus.PENDING,
                null,
                null
        );

        var entity = entityMapper.toEntity(domain);
        // entityMapper set null id; status will be set the true value in repo.insert
        return repo.insert(entity)
                .map(entityMapper::toDomain)
                .map(apiMapper::toResponse);
    }

    public Mono<CredentialResponse> getOne(long userId, long id) {
        return repo.findByIdAndUser(id, userId)
                .switchIfEmpty(Mono.error(new NotFoundException("Credential", id)))
                .map(entityMapper::toDomain)
                .map(apiMapper::toResponse);
    }

    public Mono<CursorPage<CredentialResponse>> list(long userId,
                                                     CredentialType type,
                                                     CredentialStatus status,
                                                     int limit,
                                                     String cursor) {

        int safeLimit = Math.min(Math.max(limit, 1), 100);

        var decoded = CursorCodec.decode(cursor).orElse(null);
        OffsetDateTime cAt = decoded == null ? null : decoded.createdAt();
        Long cId = decoded == null ? null : decoded.id();

        String typeStr = (type == null) ? null : type.name();
        String statusStr = status == null ? null : status.name();

        return repo.list(userId, typeStr, statusStr, safeLimit, cAt, cId)
                .map(entityMapper::toDomain)
                .collectList()
                .map(list -> {
                    List<CredentialResponse> items = list.stream().map(apiMapper::toResponse).toList();
                    String nextCursor = null;
                    if (!list.isEmpty()) {
                        Credential last = list.get(list.size() - 1);
                        nextCursor = CursorCodec.encode(last.createdAt(), last.id().value());
                    }
                    return new CursorPage<>(items, nextCursor);
                });
    }

    public Mono<Void> softDelete(long userId, long id) {
        return repo.findByIdAndUser(id, userId)
                .switchIfEmpty(Mono.error(new NotFoundException("Credential", id)))
                .map(entityMapper::toDomain)
                .flatMap(domain -> {
                    if (!domain.canSoftDelete()) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT, "Only allowed when status is PENDING or REJECTED"));
                    }
                    return repo.softDelete(id, userId).then();
                });
    }

    // Admin (without auth by assignment)
    public Mono<Void> adminUpdateStatus(long id, CredentialStatus status) {
        if (status != CredentialStatus.APPROVED && status != CredentialStatus.REJECTED) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status"));
        }
        return repo.updateStatus(id, status)
                .flatMap(rows -> rows == 0
                        ? Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"))
                        : Mono.empty());
    }

}
