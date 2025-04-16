package com.example.snsbackend.dto;

import com.example.snsbackend.common.validation.NoBlankInList;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ImageRequest {
    @NotNull(message = "The list cannot be null")
    @NoBlankInList
    @NotEmpty
    private List<String> publicIds;
}
