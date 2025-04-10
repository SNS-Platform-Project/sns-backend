package com.example.snsbackend.dto;

import com.example.snsbackend.common.validation.NoBlankInList;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomRequest {
    @NotNull
    private String name;
    @NoBlankInList
    @NotEmpty
    private List<String> userIds;
}
