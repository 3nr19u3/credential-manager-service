package com.devpull.credentialmanagerservice.api.v1.credentials.dto;

import com.devpull.credentialmanagerservice.domain.credential.Credential;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CredentialApiMapper {

    @Mapping(target = "id", expression = "java(c.id() == null ? null : c.id().value())")
    @Mapping(target = "license_number", source = "licenseNumber")
    @Mapping(target = "expiry_date", source = "expiryDate")
    @Mapping(target = "created_at", source = "createdAt")
    CredentialResponse toResponse(Credential c);
}
