package com.foody.commentservice.controller;

import com.foody.commentservice.business.CommentService;
import com.foody.commentservice.dto.CommentRequest;
import com.foody.commentservice.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private  final WebSocketController wsCtr;
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request) {
        CommentResponse response = commentService.createComment(request);
        wsCtr.broadcastComment("NEW THJIGN", request.getRecipeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @RequestBody CommentRequest request) {
        CommentResponse response = commentService.updateComment(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByRecipeId(@PathVariable Long recipeId) {
        List<CommentResponse> comments = commentService.getCommentsByRecipeId(recipeId);
        
        return ResponseEntity.ok(comments);
    }
}
