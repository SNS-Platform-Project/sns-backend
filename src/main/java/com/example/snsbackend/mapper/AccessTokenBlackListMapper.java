package com.example.snsbackend.mapper;

import com.example.snsbackend.model.AccessTokenBlackList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccessTokenBlackListMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "blackAt", expression = "java(java.time.LocalDateTime.now())")
    AccessTokenBlackList toAccessTokenBlackList(String accessToken);
}
