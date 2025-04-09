package com.example.snsbackend.model;

import com.example.snsbackend.dto.PostRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id;

    @Field("type")
    private String type = "post";

    @Field("user_id")
    @JsonIgnore
    private String userId;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();  // 작성 시간

    @Field("content")
    private String content;  // 게시물 내용

    private Stat stat = new Stat();      // 통계

    @Field("hashtags")
    private List<String> hashtags;  // 해시태그

    @Field("mentions")
    private List<String> mentions;  // 멘션된 사용자

    @Field("images")
    private List<Image> images;  // 이미지

    public Post(PostRequest content) {
        this.content = content.getContent();
        this.hashtags = content.getHashtags();
        this.mentions = content.getMentions();
        this.images = content.getImages();
    }
}
