package com.web.socket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/chat")
    public String test() {
        return "chat";
    }

    @GetMapping("/game")
    public String game() {
        return "game";
    }

}
