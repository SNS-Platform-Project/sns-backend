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
public class Comment extends BaseComment {
    @Field("replies_count")
    private Integer repliesCount = 0;

    public Comment(String postId, String userId, String content) {
        this.setType("comment");
        this.setPostId(postId);
        this.setUserId(userId);
        this.setContent(content);
    }
}
