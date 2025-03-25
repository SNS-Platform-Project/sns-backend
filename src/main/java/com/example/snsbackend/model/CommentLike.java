package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@Document("comment_likes")
@CompoundIndex(def = "{'userId': 1, 'commentId': 1}", unique = true)
public class CommentLike {
    private String commentId;
    private String userId;
    private Date createdAt;
    private String postId;
}
