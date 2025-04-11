package com.example.snsbackend.model.post;

import com.example.snsbackend.dto.PostRequest;
import com.example.snsbackend.model.Image;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id;

    @Field("type")
    private String type;

    @Field("user_id")
    @JsonIgnore
    private String userId;

    @Field("created_at")
    private LocalDateTime createdAt;  // 작성 시간

    @Field("content")
    private String content;  // 게시물 내용

    private PostStat stat = new PostStat();      // 통계

    private PostEntities entities;

    @Field("images")
    private List<Image> images;  // 이미지

    @Field("original_post_id")
    private String originalPostId;     // 인용(리포스트)한 원본 트윗 ID

    private Post(String type, String userId, PostRequest content, String originalPostId) {
        this.type = type;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        switch (type) {
            case "quote":
                this.originalPostId = originalPostId;
                break;
            case "post":
                this.content = content.getContent();
                this.entities = new PostEntities();
                this.entities.setHashtags(content.getHashtags());
                this.entities.setMentions(content.getMentions());
                this.images = content.getImages();
            case "repost":
                this.originalPostId = originalPostId;
                break;
        }
    }

    public static PostBuilder original() {
        return new Builder("post");
    }

    public static PostBuilder quote(String originalPostId) {
        return new Builder("quote", originalPostId);
    }

    public static DefaultBuilder repost(String originalPostId) {
        return new Builder("repost", originalPostId);
    }

    public interface PostBuilder {
        DefaultBuilder content(PostRequest body);
    }
    public interface DefaultBuilder {
        Post by(String userId);
    }

    private static class Builder implements PostBuilder, DefaultBuilder {
        private final String type;
        private PostRequest body;
        private String originalPostId;

        private Builder(String type) {
            this.type = type;
        }
        private Builder(String type, String originalPostId) {
            this.type = type;
            this.originalPostId = originalPostId;
        }

        @Override
        public Post by(String userId) {
            return new Post(type, userId, body, originalPostId);
        }

        @Override
        public Builder content(PostRequest body) {
            this.body = body;
            return this;
        }
    }
}
