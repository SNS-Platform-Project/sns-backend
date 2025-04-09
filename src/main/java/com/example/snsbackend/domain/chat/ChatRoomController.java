package com.example.snsbackend.domain.chat;

import com.example.snsbackend.dto.ApiResponse;
import com.example.snsbackend.dto.ChatRoomRequest;
import com.example.snsbackend.dto.PageParam;
import com.example.snsbackend.model.ChatRoom;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoom>> createChatroom(@Valid @RequestBody ChatRoomRequest request) {
        return ApiResponse.success(chatRoomService.createChatRoom(request));
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getChatRooms(@ModelAttribute PageParam param) {
        return ApiResponse.success(chatRoomService.getChatRooms(param));
    }
}
