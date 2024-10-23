package com.web.socket.domain;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class User {
    private String id;  // 고유 세션 ID
    private String name;  // 유저 이름 (optional)
    private WebSocketSession session;  // WebSocket 세션

    public User(String id, String name, WebSocketSession session) {
        this.id = id;
        this.name = name;
        this.session = session;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public boolean isSessionOpen() {
        return session.isOpen();
    }

    public void sendMessage(String message) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", session=" + session +
                '}';
    }
}
