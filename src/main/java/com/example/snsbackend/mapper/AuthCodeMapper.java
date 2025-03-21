package com.example.snsbackend.mapper;

import com.example.snsbackend.model.AuthCode;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthCodeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "email")
    @Mapping(target = "authCode", source = "code")
    AuthCode toAuthCode(String email, String code);

    @AfterMapping
    default void setEmailVerified(@MappingTarget AuthCode authCode){ authCode.setEmailVerified(false); }
}
