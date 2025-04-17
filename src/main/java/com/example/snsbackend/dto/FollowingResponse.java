package com.example.snsbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowingResponse {
    private String id;
    private String userId;
    private List<FollowResponse> followings;

    public FollowingResponse(String id, String userId, List<FollowResponse> followings) {
        this.id = id;
        this.userId = userId;
        this.followings = followings;
    }
}
