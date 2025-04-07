package com.example.snsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class Timeline {
    private LocalDateTime createdAt;
    private Post post;
    private String repostedByUserId;
}
