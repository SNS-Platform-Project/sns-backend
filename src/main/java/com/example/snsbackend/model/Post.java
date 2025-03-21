package com.example.snsbackend.model;

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

    private User user;

    @Field("created_at")
    private Date createdAt;  // 작성 시간
}
