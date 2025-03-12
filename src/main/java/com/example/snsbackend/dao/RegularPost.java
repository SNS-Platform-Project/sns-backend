package com.example.snsbackend.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collation = "posts")
public class RegularPost {
    @Id
    private String id;

    @Field("user")
    private User user;

    @Field("type")
    private String type;    // 게시물 타입 (post, quote, repost)

    @Field("content")
    private String content;  // 게시물 내용

    @Field("created_at")
    private Date createdAt;  // 작성 시간

    @Field("likes_count")
    private int likesCount;  // 좋아요 수

    @Field("repost_count")
    private int repostCount;  // 재게시 수

    @Field("comments_count")
    private int commentsCount;  // 댓글 수

    @Field("views_count")
    private int viewsCount;  // 노출 수

    @Field("reactions_count")
    private int reactionsCount;  // 반응 수

    @Field("hashtags")
    private List<String> hashtags;  // 해시태그

    @Field("mentions")
    private List<String> mentions;  // 멘션된 사용자

    @Field("images")
    private List<String> images;  // 멘션된 사용자
}
