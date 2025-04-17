package com.example.snsbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowerResponse {
    private String id;
    private String userId;
    private List<FollowResponse> followers;

    public FollowerResponse(String id, String userId, List<FollowResponse> followers) {
        this.id = id;
        this.userId = userId;
        this.followers = followers;
    }
}
