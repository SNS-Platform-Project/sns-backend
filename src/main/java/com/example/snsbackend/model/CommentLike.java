package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@Document("comment_likes")
public class CommentLike {
    private String commentId;
    private String userId;
    private Date createdAt;
}
