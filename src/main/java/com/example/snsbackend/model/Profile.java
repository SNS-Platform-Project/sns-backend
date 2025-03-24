package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "profiles")
public class Profile {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String bio;

    @Field("profile_picture_url")
    private String profilePictureUrl;

    @Field(name = "created_at")
    private LocalDateTime createdAt;

    @Field(name = "last_active")
    private LocalDateTime lastActive;

    @Field(name = "followers_count")
    private Integer followersCount;

    @Field(name = "following_count")
    private Integer followingCount;

    @Field(name = "social_links")
    private String socialLinks;

    @Field(name = "hashed_password")
    private String hashedPassword;
}
