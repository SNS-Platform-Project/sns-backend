package com.example.snsbackend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
public class Stat {
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

    @Field("shared_count")
    private int sharedCount;    // 공유 수
}
