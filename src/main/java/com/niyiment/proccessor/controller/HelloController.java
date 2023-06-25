package com.niyiment.proccessor.controller;

import com.niyiment.proccessor.domain.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HelloController {
    private final SimpMessagingTemplate template;

    @MessageMapping("/request") //client send request
    @SendTo("/topic/messages") //server send response
    public MessageDTO getMessages(MessageDTO dto){
        return dto;

    }

    @PostMapping("/send") //client send request
    public ResponseEntity<Void> sendMessage(@RequestBody MessageDTO message) {
        template.convertAndSend("/topic/message", message); //server send response
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MessageMapping("/sendMessage")
    public void receiveMessageFromClient(@Payload  MessageDTO message) {
        log.info("Received message: {}", message.toString());
    }

    @SendTo("/topic/message")
    public MessageDTO sendMessageToClient() {

        return new MessageDTO("Hello");
    }

}
