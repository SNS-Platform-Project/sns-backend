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
public class Reply extends BaseComment {
    @Field("parent_id")
    private String parentId;

    public Reply(String postId, String userId, String content, String parentId) {
        this.setType("reply");
        this.setPostId(postId);
        this.setUserId(userId);
        this.setContent(content);
        this.parentId = parentId;
    }
}
