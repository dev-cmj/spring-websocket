package com.web.socket.handler;

import com.web.socket.domain.ChatRoom;
import com.web.socket.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.net.URLDecoder.decode;
@Component
public class MultiChatRoomHandler extends TextWebSocketHandler {

    // 모든 채팅방을 관리하는 맵 (방 이름 -> ChatRoom 객체)
    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomName = extractRoomName(session);
        ChatRoom chatRoom = chatRooms.computeIfAbsent(roomName, ChatRoom::new);

        System.out.println("Chat room created or fetched: " + roomName);
        System.out.println("Total chat rooms: " + chatRooms.size());  // 현재 방의 수 출력

        // User 객체 생성 후 방에 추가
        User user = new User(session.getId(), "익명", session);
        chatRoom.addUser(user);

        System.out.println("chatRooms: " + chatRooms);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String roomName = extractRoomName(session);
        ChatRoom chatRoom = chatRooms.get(roomName);

        if (chatRoom != null) {
            chatRoom.broadcastMessage("사용자 " + session.getId() + ": " + message.getPayload());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String roomName = extractRoomName(session);
        ChatRoom chatRoom = chatRooms.get(roomName);

        if (chatRoom != null) {
            // 유저 삭제
            User userToRemove = findUserBySession(chatRoom, session);
            if (userToRemove != null) {
                chatRoom.removeUser(userToRemove);
                chatRoom.broadcastMessage("사용자가 퇴장했습니다: " + userToRemove.getId());
            }

            // 방에 유저가 없으면 방 삭제
            if (chatRoom.getUserCount() == 0) {
                chatRooms.remove(roomName);
            }
        }

        System.out.println("connection closed: " + session.getId());
    }

    private User findUserBySession(ChatRoom chatRoom, WebSocketSession session) {
        return chatRoom.getUsers().stream()
                .filter(user -> user.getSession().getId().equals(session.getId()))
                .findFirst()
                .orElse(null);
    }

    private String extractRoomName(WebSocketSession session) throws UnsupportedEncodingException {
        String uri = session.getUri().toString();
        uri = decode(uri, StandardCharsets.UTF_8);
        return uri.substring(uri.lastIndexOf('/') + 1);
    }

    public Set<ChatRoom> getChatRooms() {
        Set<ChatRoom> rooms = new HashSet<>(chatRooms.values());
        System.out.println("Rooms in getChatRooms(): " + rooms.size());  // 방의 개수 출력
        for (ChatRoom room : rooms) {
            System.out.println("Room name in getChatRooms: " + room.getRoomName());
        }
        return rooms;
    }

    // 특정 방의 유저 리스트를 반환
    public Set<User> getUsersInRoom(String roomName) {
        ChatRoom chatRoom = chatRooms.get(roomName);
        return (chatRoom != null) ? chatRoom.getUsers() : Collections.emptySet();
    }

    // 방의 인원 수 반환
    public int getRoomMemberCount(String roomName) {
        ChatRoom chatRoom = chatRooms.get(roomName);
        return (chatRoom != null) ? chatRoom.getUserCount() : 0;
    }
}