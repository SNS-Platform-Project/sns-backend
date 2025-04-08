package com.example.snsbackend.domain.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatSubInterceptor implements ChannelInterceptor {
    private final ChatRoomService chatRoomService;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String userId = accessor.getUser().getName();
            String destination = accessor.getDestination();

            if (destination != null && destination.startsWith("/topic/chat/")) {
                String roomId = destination.replace("/topic/chat/", "");
                // 🔍 채팅방에 속해 있는지 검증
                if (userId == null || !chatRoomService.isUserInRoom(userId, roomId)) {
                    log.warn("Unauthorized subscription attempt by user: {}", userId);
                    throw new IllegalArgumentException("You are not allowed to subscribe to this chat room.");
                }
                log.info("User {} accesses Chat Room {}", userId, roomId);
            }
        }

        return message;
    }
}
