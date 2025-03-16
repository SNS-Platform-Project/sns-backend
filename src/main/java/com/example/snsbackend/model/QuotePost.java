package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuotePost extends Post {
    @Field("original_post")
    private String original_post_id;     // 인용한 원본 트윗 ID

    @Field("content")
    private String content;  // 게시물 내용

    private Stat stat;      // 통계

    @Field("hashtags")
    private List<String> hashtags;  // 해시태그

    @Field("mentions")
    private List<String> mentions;  // 멘션된 사용자

    @Field("images")
    private List<String> images;  // 이미지
}
