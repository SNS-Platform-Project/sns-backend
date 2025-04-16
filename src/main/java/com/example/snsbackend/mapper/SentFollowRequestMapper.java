package com.example.snsbackend.mapper;

import com.example.snsbackend.model.SentFollowRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SentFollowRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    SentFollowRequest toSentFollowRequest(String userId);
}
