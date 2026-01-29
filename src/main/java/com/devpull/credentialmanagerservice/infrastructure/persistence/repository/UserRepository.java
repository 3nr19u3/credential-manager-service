package com.devpull.credentialmanagerservice.infrastructure.persistence.repository;

import com.devpull.credentialmanagerservice.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {
    Mono<UserEntity> findByEmail(String email);
}
