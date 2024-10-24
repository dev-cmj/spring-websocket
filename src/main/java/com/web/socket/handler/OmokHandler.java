package com.web.socket.handler;

import com.web.socket.domain.OmokRoom;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OmokHandler extends TextWebSocketHandler {

    private final Map<String, OmokRoom> gameRooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = extractRoomId(session);
        OmokRoom room = gameRooms.computeIfAbsent(roomId, OmokRoom::new);
        room.addPlayer(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = extractRoomId(session);
        OmokRoom room = gameRooms.get(roomId);

        if (room != null) {
            room.removePlayer(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); // row,column
        OmokRoom room = getRoom(session);
        room.processMove(session, payload);
    }

    private String extractRoomId(WebSocketSession session) {
        // WebSocket URI에서 roomId 추출 (URI는 ws://localhost:8080/omok/{roomId} 형식)
        String uri = session.getUri().toString();
        return uri.substring(uri.lastIndexOf("/") + 1);
    }

    private OmokRoom getRoom(WebSocketSession session) {
        String roomId = extractRoomId(session);
        return gameRooms.get(roomId);
    }
}