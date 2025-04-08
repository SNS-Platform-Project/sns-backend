package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@AllArgsConstructor
@Document("comment_likes")
@CompoundIndex(def = "{'userId': 1, 'commentId': 1}", unique = true)
public class CommentLike {
    @Field("comment_id")
    private String commentId;
    @Field("user_id")
    private String userId;
    @Field("created_at")
    private Date createdAt;
    @Field("post_id")
    private String postId;
}
