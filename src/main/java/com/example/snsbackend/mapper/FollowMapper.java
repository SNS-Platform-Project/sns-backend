package com.example.snsbackend.mapper;

import com.example.snsbackend.model.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowMapper {
    @Mapping(target = "followId", source = "followId")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Follow toFollow(String followId);
}
