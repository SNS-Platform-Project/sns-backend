package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "post_likes")
@CompoundIndex(def = "{'userId': 1, 'postId': 1}", unique = true)
public class PostLike {
    @Field("post_id")
    private String postId;

    @Field("user_id")
    private String userId;

    @Field("created_at")
    private Date createdAt;
}
