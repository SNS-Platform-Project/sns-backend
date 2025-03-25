package com.example.snsbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@Document(collection = "posts")
public abstract class Post {
    @Id
    private String id;

    @Field("type")
    private String type;

    @Field("user_id")
    @JsonIgnore
    private String userId;

    @Field("created_at")
    private Date createdAt;  // 작성 시간
}
