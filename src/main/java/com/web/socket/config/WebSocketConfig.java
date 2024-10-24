package com.web.socket.config;

import com.web.socket.handler.ChatHandler;
import com.web.socket.handler.MultiChatRoomHandler;
import com.web.socket.handler.RockPaperScissorsHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;
    private final RockPaperScissorsHandler rockPaperScissorsHandler;
    private final MultiChatRoomHandler multiChatRoomHandler;

    public WebSocketConfig(ChatHandler chatHandler, RockPaperScissorsHandler rockPaperScissorsHandler, MultiChatRoomHandler multiChatRoomHandler) {
        this.chatHandler = chatHandler;
        this.rockPaperScissorsHandler = rockPaperScissorsHandler;
        this.multiChatRoomHandler = multiChatRoomHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        //예제 1. 익명 채팅방
        registry.addHandler(chatHandler, "/ws/anonymous-chat");
        //예제 2. 가위바위보 게임
        registry.addHandler(rockPaperScissorsHandler, "/ws/rps");
        //예제 3. 채팅방
        registry.addHandler(multiChatRoomHandler, "/ws/chat/{roomName}").setAllowedOrigins("*");
    }
}
