package com.example.snsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewDataRequest {
    @NotBlank
    private String newData;
}
