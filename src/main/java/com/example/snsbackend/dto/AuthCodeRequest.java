package com.example.snsbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthCodeRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String authCode;
}
