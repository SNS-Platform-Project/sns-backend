package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Document("comments")
public class Comment {
    @Field("post_id")
    private String postId;
    @Field("user_id")
    private String userId;
    @Field("content")
    private String content;
    @Field("parent_id")
    private String parentId;    // 대댓글의 경우 부모 댓글 ID, 댓글의 경우 null
    @Field("created_at")
    private Date createdAt;
    @Field("likes_count")
    private int likesCount;
}
