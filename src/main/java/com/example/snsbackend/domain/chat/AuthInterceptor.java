package com.example.snsbackend.domain.chat;

import com.example.snsbackend.jwt.JwtProvider;
import com.example.snsbackend.jwt.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements ChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String token = TokenUtils.extractJwt(accessor.getFirstNativeHeader("Authorization"));
            String userId = jwtProvider.parseToken(token).getSubject();
            accessor.setUser(() -> userId);
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            log.info("Session closed: " + sessionId);
        }

        return message;
    }

}
