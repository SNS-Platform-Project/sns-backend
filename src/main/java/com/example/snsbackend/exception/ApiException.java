package com.example.snsbackend.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ApiErrorType errorType;
    private final String detailMessage;
    private final String details;

    public ApiException(ApiErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.detailMessage = null;
        this.details = null;
    }

    public ApiException(ApiErrorType errorType, String details) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.detailMessage = null;
        this.details = details;
    }

    public ApiException(ApiErrorType errorType, String details, String detailMessage) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.detailMessage = detailMessage;
        this.details = details;
    }
}
