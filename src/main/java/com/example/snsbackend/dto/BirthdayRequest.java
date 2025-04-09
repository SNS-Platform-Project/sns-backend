package com.example.snsbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BirthdayRequest {
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}
