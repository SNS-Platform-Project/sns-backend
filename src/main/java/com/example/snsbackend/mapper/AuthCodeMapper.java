package com.example.snsbackend.mapper;

import com.example.snsbackend.model.AuthCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthCodeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "email")
    @Mapping(target = "authCode", source = "code")
    AuthCode toAuthCode(String email, String code);
}
