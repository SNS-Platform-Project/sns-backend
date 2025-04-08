package com.example.snsbackend.exception;

import com.example.snsbackend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<String>> exceptionHandler(ApiException e) {
        ApiErrorType errorType = e.getErrorType();
        String details = e.getDetails();
        String detailMessage = e.getDetailMessage();

        return ApiResponse.status(
          errorType.getStatus(),
                errorType.getMessage() + (details != null ? " (" + details + ")" : ""),
                detailMessage
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> exceptionHandler(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ApiResponse.status(HttpStatus.BAD_REQUEST, errorMessage, null);
    }
}
