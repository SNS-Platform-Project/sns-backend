package com.example.snsbackend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Image {
    @NotBlank
    private String url;
    @NotBlank
    private String public_id;
}
