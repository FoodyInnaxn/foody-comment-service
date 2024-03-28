package com.foody.commentservice.controller;

import com.foody.commentservice.business.CommentService;
import com.foody.commentservice.dto.CommentResponse;
import com.foody.commentservice.persistence.entity.CommentEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.foody.commentservice.dto.CommentRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor
public class WebSocketController {

    private final CommentService commentService;
    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/comments")
    public void broadcastComment(@Payload String comment, Long recipeId) {
        System.out.println("Received comment for recipeId: " + recipeId + ", Comment: " + comment);
            this.simpMessagingTemplate.convertAndSend("/topic/news/" + recipeId, comment);
    }
}
