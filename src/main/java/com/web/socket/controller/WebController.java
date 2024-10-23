package com.web.socket.controller;

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
        Set<String> chatRoomNames = chatRoomHandler.getChatRoomNames();
        model.addAttribute("rooms", chatRoomNames);
        return "chat";
    }

    @GetMapping("/chat/{roomName}")
    public String chatRoom(@PathVariable String roomName, Model model) {
        chatRoomHandler.createChatRoomIfNotExists(roomName);
        model.addAttribute("roomName", roomName);
        return "chatroom";
    }

}
