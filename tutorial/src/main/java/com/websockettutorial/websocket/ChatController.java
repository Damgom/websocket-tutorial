package com.websockettutorial.websocket;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/chat")
    public String chatGet() {
        return "chat";
    }
}
