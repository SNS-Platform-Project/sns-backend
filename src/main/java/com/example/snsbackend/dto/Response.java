package com.example.snsbackend.dto;

import lombok.Getter;

@Getter
public class Response<T> {
    private boolean success;
    private String message;
    private T data;

    private Response(Builder<T> builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.data = builder.data;
    }

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;

        public Builder() {
            this.success = true;
        }

        public Builder<T> setSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> setData(T data) {
            this.data = data;
            return this;
        }

        public Response<T> build() {
            return new Response<>(this);
        }
    }
}
