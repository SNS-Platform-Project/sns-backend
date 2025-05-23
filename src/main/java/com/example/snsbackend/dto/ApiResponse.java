package com.example.snsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Getter
public class ApiResponse<T> {
    private final HttpStatusCode status;
    private final String message;
    private final T body;

    public ApiResponse(HttpStatusCode status, String message, T body) {
        this.status = status;
        this.message = (message != null) ? message : getDefaultMessage((HttpStatus) status);
        this.body = body;
    }
    public static ResponseEntity<ApiResponse<Void>> success() {
        return createResponseEntity(HttpStatus.OK, null, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T body) {
        return createResponseEntity(HttpStatus.OK, null, body);
    }

    public static ResponseEntity<ApiResponse<Void>> notFound() {
        return createResponseEntity(HttpStatus.NOT_FOUND, null, null);
    }

    public static ResponseEntity<ApiResponse<Void>> notFound(String msg) {
        return createResponseEntity(HttpStatus.NOT_FOUND, msg, null);
    }

    public static ResponseEntity<ApiResponse<Void>> conflict() {
        return createResponseEntity(HttpStatus.CONFLICT, null, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> status(HttpStatusCode status, String message, T body) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message, body));
    }

    public static <T> ResponseEntity<ApiResponse<T>> status(HttpStatusCode status, String message) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message, null));
    }

    private static <T> ResponseEntity<ApiResponse<T>> createResponseEntity(HttpStatus status, String message, T body) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(status, message, body));
    }

    private static String getDefaultMessage(HttpStatus status) {
        return switch (status) {
            case OK -> "정상적으로 처리되었습니다.";
            case NOT_FOUND -> "리소스를 찾을 수 없습니다.";
            case CONFLICT -> "중복된 요청입니다.";
            case BAD_REQUEST -> "요청 형식이 잘못되었습니다.";
            case INTERNAL_SERVER_ERROR -> "서버 내부 오류가 발생했습니다.";
            default -> "요청 처리 중 예상치 못한 오류가 발생했습니다.";
        };
    }
}
