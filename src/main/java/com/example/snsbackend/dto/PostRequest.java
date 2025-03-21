package com.example.snsbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data
public class PostRequest {
    @Valid
    @Size(min = 1, max = 300, message = "Content must be between 1 and 300 characters")
    private String content;
    private List<String> hashtags;
    private List<String> mentions;
    private List<String> images;
}
