package com.example.snsbackend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
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
    @Field("replies_count")
    private Number repliesCount;
}
