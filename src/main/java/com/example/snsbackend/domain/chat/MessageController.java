package com.example.snsbackend.domain.chat;

import com.example.snsbackend.dto.SendMessage;
import com.example.snsbackend.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageController {
    private final MessageService messageService;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public Message sendMessage(@DestinationVariable String roomId, @Payload SendMessage sendMessage, Principal principal) {
        String senderId = principal.getName();
        log.info("메시지 수신: {}", sendMessage.getContent());

        return messageService.createMessage(roomId, senderId, sendMessage);
    }
}
