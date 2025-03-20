package com.example.snsbackend.mapper;

import com.example.snsbackend.jwt.JwtInfo;
import com.example.snsbackend.model.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "refreshToken", source = "jwtInfo.refreshToken")
    @Mapping(target = "issuedAt", source = "jwtInfo.refreshIseAt")
    @Mapping(target = "expiredAt", source = "jwtInfo.refreshExpAt")
    RefreshToken toRefreshToken(String userId, JwtInfo jwtInfo);
}
