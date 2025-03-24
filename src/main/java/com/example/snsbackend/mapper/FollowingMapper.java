package com.example.snsbackend.mapper;

import com.example.snsbackend.model.Following;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    Following toFollowing(String userId);
}
