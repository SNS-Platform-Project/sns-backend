package com.example.snsbackend.mapper;

import com.example.snsbackend.model.ReceivedFollowRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceivedFollowRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    ReceivedFollowRequest toFollowRequest(String userId);
}
