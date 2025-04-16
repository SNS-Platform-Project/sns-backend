package com.example.snsbackend.mapper;

import com.example.snsbackend.dto.RegisterRequest;
import com.example.snsbackend.model.Profile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "register.username")
    @Mapping(target = "email", source = "register.email")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Profile toProfile(RegisterRequest register);

    @AfterMapping
    default void setDefaults(@MappingTarget Profile profile) {
        profile.setBio("");
        profile.setProfilePicture(null);
        profile.setBirthday(null);
        profile.setPrivate(false);
        profile.setFollowersCount(0);
        profile.setFollowingCount(0);
        profile.setSocialLinks("");
    }
}
