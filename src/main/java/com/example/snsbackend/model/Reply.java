package com.example.snsbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Setter
@Getter
@Document(collection = "comments")
public class Reply {
    @Id
    private String id;
    @Field("type")
    @JsonIgnore
    private String type = "reply";
    @Field("post_id")
    private String postId;
    @Field("user_id")
    private String userId;
    @Field("content")
    private String content;
    @Field("parent_id")
    private String parentId;
    @Field("created_at")
    private Date createdAt = new Date();
    @Field("likes_count")
    private int likesCount = 0;

    public Reply(String postId, String userId, String content, String parentId) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.parentId = parentId;
    }
}
