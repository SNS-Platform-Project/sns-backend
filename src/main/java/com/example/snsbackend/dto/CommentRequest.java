package com.example.snsbackend.dto;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CommentRequest {
    @Valid
    private String content;
    private String parentId;    // 대댓글인 경우 부모 댓글 ID
}
