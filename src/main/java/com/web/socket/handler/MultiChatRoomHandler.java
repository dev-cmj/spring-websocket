package com.web.socket.handler;

import com.web.socket.domain.ChatRoom;
import com.web.socket.domain.User;
import com.web.socket.generator.NicknameGenerator;
import com.web.socket.service.ChatRoomService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static java.net.URLDecoder.decode;

@Component
public class MultiChatRoomHandler extends TextWebSocketHandler {

    private final ChatRoomService chatRoomService;

    public MultiChatRoomHandler(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomName = extractRoomName(session);
        User user = new User(session.getId(), NicknameGenerator.createRandomNickName(), session);
        chatRoomService.addUserToRoom(roomName, user);
        // 채팅방에 입장하면 채팅방에 있는 모든 유저에게 입장 메시지 전송
        chatRoomService.getUsersInRoom(roomName).forEach(u -> {
            try {
                u.getSession().sendMessage(new TextMessage(user.getName() + "님이 입장했습니다."));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String roomName = extractRoomName(session);
        ChatRoom chatRoom = chatRoomService.getOrCreateRoom(roomName);
        User user = chatRoomService.getUsersInRoom(roomName).stream()
                .filter(u -> u.getSession().getId().equals(session.getId()))
                .findFirst().orElse(null);
        LocalDateTime now = LocalDateTime.now();
        if (chatRoom != null) {
            chatRoom.broadcastMessage(user.getName() + " : " + message.getPayload() + " (" + (now.getHour() < 12 ? "오전" : "오후") + " " + now.getHour() + ":" + now.getMinute() + ")");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String roomName = extractRoomName(session);
        User user = chatRoomService.getUsersInRoom(roomName).stream()
                .filter(u -> u.getSession().getId().equals(session.getId()))
                .findFirst().orElse(null);
        if (user != null) {
            chatRoomService.removeUserFromRoom(roomName, user);
            System.out.println("User " + user.getId() + " removed from room: " + roomName);
        }
    }

    private String extractRoomName(WebSocketSession session) throws UnsupportedEncodingException {
        String uri = session.getUri().toString();
        uri = decode(uri, StandardCharsets.UTF_8);
        return uri.substring(uri.lastIndexOf('/') + 1);
    }

}