package com.web.socket.controller;

import com.web.socket.domain.ChatRoom;
import com.web.socket.domain.User;
import com.web.socket.handler.MultiChatRoomHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@Controller
public class WebController {

    private final MultiChatRoomHandler chatRoomHandler;

    public WebController(MultiChatRoomHandler chatRoomHandler) {
        this.chatRoomHandler = chatRoomHandler;
    }

    @GetMapping("/anonymous-chat")
    public String test() {
        return "anonymous-chat";
    }

    @GetMapping("/game")
    public String game() {
        return "game";
    }


    @GetMapping("/chat")
    public String chatRooms(Model model) {
        Set<ChatRoom> rooms = chatRoomHandler.getChatRooms();

        // 방 목록이 잘 조회되고 있는지 로그 출력
        System.out.println("Fetched chat rooms count: " + rooms.size());
        for (ChatRoom room : rooms) {
            System.out.println("Room: " + room.getRoomName());
        }

        model.addAttribute("rooms", rooms);
        return "chat";  // chat 페이지로 이동
    }

    @GetMapping("/chat/{roomName}")
    public String chatRoom(@PathVariable String roomName, Model model) {
        Set<User> users = chatRoomHandler.getUsersInRoom(roomName);
        model.addAttribute("roomName", roomName);
        model.addAttribute("users", users);
        return "chatroom";
    }

}
