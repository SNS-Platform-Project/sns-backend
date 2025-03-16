package com.example.snsbackend.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class PostRequest {
    @Valid
    private String content;
    private List<String> hashtags;
    private List<String> mentions;
}
