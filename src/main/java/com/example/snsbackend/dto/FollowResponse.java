package com.example.snsbackend.dto;

import com.example.snsbackend.model.Follow;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({"followId", "username", "createdAt"})
public class FollowResponse {
    private String followId;
    private String username;
    private LocalDateTime createdAt;

    public FollowResponse(Follow follow, String username) {
        this.followId = follow.getFollowId();
        this.username = username;
        this.createdAt = follow.getCreatedAt();
    }
}