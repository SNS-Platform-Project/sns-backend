package com.example.snsbackend.dto;

import com.example.snsbackend.model.Post;
import com.example.snsbackend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PostResponse {
    private Post post;
    private User user;
}
