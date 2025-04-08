package com.example.snsbackend.config;

import com.example.snsbackend.domain.chat.AuthInterceptor;
import com.example.snsbackend.domain.chat.ChatSubInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private final AuthInterceptor authInterceptor;
    private final ChatSubInterceptor chatSubInterceptor;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /portfolio는 웹소켓(또는 SockJS)이 있는 엔드포인트의 HTTP URL입니다
        // 클라이언트가 WebSocket 핸드셰이크에 연결해야 합니다
        registry.addEndpoint("/portfolio")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 목적지 헤더가 /app으로 시작하는 STOMP 메시지는 다음으로 라우팅됩니다
        // @Controller 클래스의 @MessageMapping 메서드
        config.setApplicationDestinationPrefixes("/app");
        // 구독 및 방송에 내장된 메시지 브로커를 사용하세요
        // 목적지 헤더가 /topic 또는 /queue로 시작하는 메시지를 브로커에게 라우팅합니다
        config.enableSimpleBroker("/topic", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authInterceptor, chatSubInterceptor);
    }
}
