package com.devpull.credentialmanagerservice.infrastructure.persistence.entity;

import com.devpull.credentialmanagerservice.domain.credential.CredentialStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Table("credentials")
@Getter
@Setter
public class CredentialEntity {

    @Id
    private Long id;
    private Long userId;
    private String type;
    private String issuer;
    private String licenseNumber;
    private LocalDate expiryDate;
    private CredentialStatus status;
    private OffsetDateTime deletedAt;
    private OffsetDateTime createdAt;

}
