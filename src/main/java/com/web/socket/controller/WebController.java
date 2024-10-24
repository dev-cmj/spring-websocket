package com.web.socket.controller;

import com.web.socket.domain.ChatRoom;
import com.web.socket.domain.User;
import com.web.socket.service.ChatRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@Controller
public class WebController {

    private final ChatRoomService chatRoomService;

    public WebController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
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
        Set<ChatRoom> rooms = chatRoomService.getAllChatRooms();

        System.out.println("rooms = " + rooms);

        model.addAttribute("rooms", rooms);
        return "chat";  // chat 페이지로 이동
    }

    @GetMapping("/chat/{roomName}")
    public String chatRoom(@PathVariable String roomName, Model model) {
        Set<User> users = chatRoomService.getUsersInRoom(roomName);
        model.addAttribute("roomName", roomName);
        model.addAttribute("users", users);
        return "chatroom";
    }

    @GetMapping("/omok")
    public String omok() {
        return "omok";
    }

}
