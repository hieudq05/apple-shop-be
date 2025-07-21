package com.web.appleshop.controller;

import com.web.appleshop.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {

        ChatMessage responseMessage = new ChatMessage();
        responseMessage.setSender("Bot");
        responseMessage.setContent(chatMessage.getContent());

        return responseMessage;
    }
}
