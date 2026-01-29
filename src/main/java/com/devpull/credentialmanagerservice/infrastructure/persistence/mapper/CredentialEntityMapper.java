package com.devpull.credentialmanagerservice.infrastructure.persistence.mapper;

import com.devpull.credentialmanagerservice.domain.credential.Credential;
import com.devpull.credentialmanagerservice.infrastructure.persistence.entity.CredentialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CredentialEntityMapper {

    @Mapping(target = "id", expression = "java(e.getId() == null ? null : new CredentialId(e.getId()))")
    @Mapping(target = "userId", expression = "java(new UserId(e.getUserId()))")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "type", expression = "java(com.devpull.credentialmanagerservice.domain.credential.CredentialType.valueOf(e.getType()))")
    @Mapping(target = "status", source = "status")
    Credential toDomain(CredentialEntity e);

    @Mapping(target = "id", expression = "java(c.id() == null ? null : c.id().value())")
    @Mapping(target = "userId", expression = "java(c.userId().value())")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    @Mapping(target = "type", expression = "java(c.type().name())")
    @Mapping(target = "status", source = "status")
    CredentialEntity toEntity(Credential c);
}
