package com.example.snsbackend.common.exception;

import com.example.snsbackend.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(final NoSuchElementException e) {
        return ApiResponse.notFound(e.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(final ResponseStatusException e) {
        return ApiResponse.status(e.getStatusCode(), e.getReason());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNotReadable(final HttpMessageNotReadableException e) {
        return ApiResponse.status(HttpStatus.BAD_REQUEST, "Invalid request body");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        // BindingResult 가져오기
        BindingResult bindingResult = e.getBindingResult();

        // ApiResponse를 통해 오류 메시지 반환
        return ApiResponse.status(HttpStatus.BAD_REQUEST, bindingResult.getFieldErrors().getFirst().getDefaultMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleInternalError(final RuntimeException e) {
        return ApiResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
