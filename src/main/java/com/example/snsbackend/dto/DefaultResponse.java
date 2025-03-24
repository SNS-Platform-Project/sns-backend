package com.example.snsbackend.dto;

import lombok.Data;

@Data
public class DefaultResponse<T> {
    private boolean success;
    private String message;
    private T data;

    DefaultResponse() {
        this.success = true;
        this.message = "";
        this.data = null;
    }
}
