package com.devpull.credentialmanagerservice.infrastructure.persistence.mapper;

import com.devpull.credentialmanagerservice.domain.user.User;
import com.devpull.credentialmanagerservice.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(target = "id", expression = "java(new UserId(e.getId()))")
    @Mapping(target = "email", expression = "java(Email.of(e.getEmail()))")
    User toDomain(UserEntity e);

    @Mapping(target = "id", expression = "java(u.id() == null ? null : u.id().value())")
    @Mapping(target = "email", expression = "java(u.email().value())")
    @Mapping(target = "passwordHash", expression = "java(u.passwordHash())")
    UserEntity toEntity(User u);
}
