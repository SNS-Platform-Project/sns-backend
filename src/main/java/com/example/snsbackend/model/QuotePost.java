package com.example.snsbackend.model;

import com.example.snsbackend.dto.PostRequest;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuotePost extends Post {
    @Field("original_post_id")
    private String originalPostId;     // 인용한 원본 트윗 ID

    public QuotePost(PostRequest content) {
        this.setContent(content.getContent());
        this.setHashtags(content.getHashtags());
        this.setMentions(content.getMentions());
        this.setImages(content.getImages());
    }
}
