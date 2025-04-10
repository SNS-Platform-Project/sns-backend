package com.example.snsbackend.dto;

import com.example.snsbackend.common.validation.NoBlankInList;
import com.example.snsbackend.model.Image;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data
public class PostRequest {
    @NotNull
    @Size(min = 1, max = 300, message = "Content must be between 1 and 300 characters")
    private String content;
    @NoBlankInList
    private List<String> hashtags;
    @NoBlankInList
    private List<String> mentions;
    private List<Image> images;
}
