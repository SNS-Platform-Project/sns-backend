package com.example.snsbackend.model;

import com.example.snsbackend.model.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Timeline {
    private LocalDateTime createdAt;
    private Post post;
    private String repostedByUserId;
}
