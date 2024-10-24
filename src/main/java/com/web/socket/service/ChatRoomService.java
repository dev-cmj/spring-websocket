package com.web.socket.service;

import com.web.socket.domain.ChatRoom;
import com.web.socket.domain.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatRoomService {
    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    public ChatRoom getOrCreateRoom(String roomName) {
        return chatRooms.computeIfAbsent(roomName, ChatRoom::new);
    }

    public Set<ChatRoom> getAllChatRooms() {
        return new HashSet<>(chatRooms.values());
    }

    public Set<User> getUsersInRoom(String roomName) {
        ChatRoom chatRoom = chatRooms.get(roomName);
        return (chatRoom != null) ? chatRoom.getUsers() : Collections.emptySet();
    }

    public void addUserToRoom(String roomName, User user) {
        ChatRoom chatRoom = getOrCreateRoom(roomName);
        chatRoom.addUser(user);
    }

    public void removeUserFromRoom(String roomName, User user) {
        ChatRoom chatRoom = chatRooms.get(roomName);
        if (chatRoom != null) {
            chatRoom.removeUser(user);
            if (chatRoom.getUserCount() == 0) {
                chatRooms.remove(roomName);
            }
        }
    }

    public int getRoomMemberCount(String roomName) {
        ChatRoom chatRoom = chatRooms.get(roomName);
        return (chatRoom != null) ? chatRoom.getUserCount() : 0;
    }
}