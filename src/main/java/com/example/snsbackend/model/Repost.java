package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "reposts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@CompoundIndex(def = "{'userId': 1, 'postId': 1}", unique = true)
public class Repost {
    @Field("user_id")
    private String userId;
    @Field("post_id")
    private String postId;      // 원본 트윗 ID
    @Field("created_at")
    private LocalDateTime createdAt;

    public Repost(String userId, String postId) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = LocalDateTime.now();
    }
}
