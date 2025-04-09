package com.example.snsbackend.dto;

import com.example.snsbackend.common.validation.NoBlackInList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class ChatRoomRequest {
    @NotNull
    private String name;
    @NoBlackInList
    @NotEmpty
    private List<String> userIds;
}
