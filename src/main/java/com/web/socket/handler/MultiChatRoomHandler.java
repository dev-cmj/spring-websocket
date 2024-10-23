package com.web.socket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.net.URLDecoder.decode;

@Component
public class MultiChatRoomHandler extends TextWebSocketHandler {

    // 채팅방을 명시적으로 생성하는 메소드
    public void createChatRoomIfNotExists(String roomName) {
        chatRooms.computeIfAbsent(roomName, key -> new java.util.concurrent.CopyOnWriteArrayList<>());
    }

    // 채팅방별로 WebSocketSession을 관리하는 Map
    private final Map<String, List<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // URL에서 방 이름을 추출
        String roomName = extractRoomName(session);
        chatRooms.computeIfAbsent(roomName, key -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(session);
        broadcastMessage(roomName, "사용자가 입장했습니다: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String roomName = extractRoomName(session);
        broadcastMessage(roomName, "사용자 " + session.getId() + ": " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String roomName = extractRoomName(session);
        List<WebSocketSession> sessions = chatRooms.get(roomName);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                chatRooms.remove(roomName);
            } else {
                broadcastMessage(roomName, "사용자가 퇴장했습니다: " + session.getId());
            }
        }
    }

    // 채팅방 내 모든 사용자에게 메시지를 전송하는 메소드
    private void broadcastMessage(String roomName, String message) throws IOException {
        List<WebSocketSession> sessions = chatRooms.get(roomName);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    private String extractRoomName(WebSocketSession session) throws UnsupportedEncodingException {
        String uri = session.getUri().toString();
        uri = decode(uri, StandardCharsets.UTF_8);
        return uri.substring(uri.lastIndexOf('/') + 1);
    }

    public Set<String> getChatRoomNames() {
        return chatRooms.keySet();
    }
}