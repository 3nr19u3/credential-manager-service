package com.devpull.credentialmanagerservice.infrastructure.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("users")
@Getter
@Setter
public class UserEntity {
    @Id
    private Long id;
    private String email;
    private String passwordHash;
    private OffsetDateTime createdAt;
}
