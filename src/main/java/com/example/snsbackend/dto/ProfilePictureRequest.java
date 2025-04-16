package com.example.snsbackend.dto;

import com.example.snsbackend.model.Image;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfilePictureRequest {
    @Valid
    @NotNull
    private Image image;
}
