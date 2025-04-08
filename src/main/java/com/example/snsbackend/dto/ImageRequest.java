package com.example.snsbackend.dto;

import com.example.snsbackend.common.validation.NoBlackInList;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ImageRequest {
    @NotNull(message = "The list cannot be null")
    @NoBlackInList
    private List<String> publicIds;
}
