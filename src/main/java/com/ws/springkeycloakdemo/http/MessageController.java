package com.ws.springkeycloakdemo.http;

import com.ws.springkeycloakdemo.dto.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message greeting(Message message) {
        return new Message("Сервер обработал сообщение: " + message.text());
    }
}
