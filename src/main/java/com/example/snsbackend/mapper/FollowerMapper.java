package com.example.snsbackend.mapper;

import com.example.snsbackend.model.Follower;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    Follower toFollower(String userId);
}
