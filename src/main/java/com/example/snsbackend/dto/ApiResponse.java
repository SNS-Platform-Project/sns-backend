package com.example.snsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ApiResponse<T> {
    private final HttpStatus status;
    private final String message;
    private final T body;

    private ApiResponse(HttpStatus status, String message, T body) {
        this.status = status;
        this.message = (message != null) ? message : getDefaultMessage(status);
        this.body = body;
    }
    public static ResponseEntity<ApiResponse<Void>> success() {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, null, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T body) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, null, body));
    }

    public static ResponseEntity<ApiResponse<Void>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, null, null));
    }

    public static ResponseEntity<ApiResponse<Void>> conflict() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(HttpStatus.CONFLICT, null, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> status(HttpStatus status, String message, T body) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message, body));
    }

    private static String getDefaultMessage(HttpStatus status) {
        return switch (status) {
            case OK -> "요청이 성공했습니다.";
            case NOT_FOUND -> "리소스를 찾을 수 없습니다.";
            case CONFLICT -> "중복된 요청입니다.";
            case BAD_REQUEST -> "잘못된 요청입니다.";
            case INTERNAL_SERVER_ERROR -> "서버 내부 오류입니다.";
            default -> "요청 처리 중 오류가 발생했습니다.";
        };
    }
}
