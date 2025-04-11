package com.example.snsbackend.model.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
public class PostStat {
    @Field("like_count")
    private int likesCount;  // 좋아요 수

    @Field("repost_count")
    private int repostCount;  // 재게시 수

    @Field("comment_count")
    private int commentsCount;  // 댓글 수

    @Field("view_count")
    private int viewCount;  // 노출 수

    @Field("shared_count")
    private int sharedCount;    // 공유 수

    @Field("bookmark_count")
    private int bookmarkCount;
}
