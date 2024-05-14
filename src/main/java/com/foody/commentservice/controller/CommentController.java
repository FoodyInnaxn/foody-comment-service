package com.foody.commentservice.controller;

import com.foody.commentservice.business.CommentService;
import com.foody.commentservice.business.exceptions.CommentNotFoundException;
import com.foody.commentservice.dto.CommentRequest;
import com.foody.commentservice.dto.CommentResponse;
import com.foody.commentservice.dto.CommentsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private  final WebSocketController wsCtr;
    @PostMapping("/{id}/operations/create")
    public ResponseEntity<CommentResponse> createComment(@PathVariable("id") Long userId, @RequestBody CommentRequest request) {
        CommentResponse response = commentService.createComment(request, userId);
        wsCtr.broadcastComment("NEW THJIGN", request.getRecipeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/operations/update/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("id") Long userId,
                                                         @PathVariable("commentId") Long commentId, @RequestBody CommentRequest request) {
        CommentResponse response = commentService.updateComment(userId, commentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/operations/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long userId,
                                              @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recipe/{recipeId}")
    public CommentsResponse getCommentsByRecipeId(
            @PathVariable Long recipeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        CommentsResponse commentsResponse = commentService.getCommentsByRecipeId(recipeId, page, size);
        if (commentsResponse.getComments().isEmpty()) {
            throw new CommentNotFoundException();
        }
        return commentsResponse;
    }
}
