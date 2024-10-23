package com.web.socket.domain;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ChatRoom {
    private String roomName;  // 채팅방 이름
    private Set<User> users;  // 방에 참여한 유저 리스트

    public ChatRoom(String roomName) {
        this.roomName = roomName;
        this.users = new HashSet<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public int getUserCount() {
        return users.size();
    }

    public void broadcastMessage(String message) throws IOException {
        for (User user : users) {
            user.sendMessage(message);
        }
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomName='" + roomName + '\'' +
                ", users=" + users +
                '}';
    }
}