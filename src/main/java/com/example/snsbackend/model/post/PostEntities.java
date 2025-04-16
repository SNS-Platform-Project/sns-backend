package com.example.snsbackend.model.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Setter
@Getter
public class PostEntities {
    @Field("hashtags")
    private List<String> hashtags;  // 해시태그

    @Field("mentions")
    private List<String> mentions;  // 멘션된 사용자
}
