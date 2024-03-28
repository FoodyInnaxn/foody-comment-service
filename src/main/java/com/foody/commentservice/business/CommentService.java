package com.foody.commentservice.business;

import com.foody.commentservice.business.exceptions.CommentNotFoundException;
import com.foody.commentservice.dto.CommentRequest;
import com.foody.commentservice.dto.CommentResponse;
import com.foody.commentservice.persistence.CommentRepository;
import com.foody.commentservice.persistence.entity.CommentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final SimpMessagingTemplate messagingTemplate;
    private final CommentRepository commentRepository;

    public CommentEntity createComment(CommentEntity comment) {
        CommentEntity savedComment = commentRepository.save(comment);
        messagingTemplate.convertAndSend("/topic/comments", savedComment);
        return savedComment;
    }

    public CommentResponse createComment(CommentRequest request) {
        CommentEntity comment = mapToComment(request);
        CommentEntity savedComment = commentRepository.save(comment);
        notifyCommentAdded(savedComment);
        return mapToCommentResponse(savedComment);
    }

    public CommentResponse updateComment(Long id, CommentRequest request) {
        CommentEntity existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException());

        // Update the existing comment with data from the request

        existingComment.setUserId(request.getUserId());
        existingComment.setRecipeId(request.getRecipeId());
        existingComment.setContent(request.getContent());
        existingComment.setParentId(request.getParentId());

        CommentEntity updatedComment = commentRepository.save(existingComment);
        return mapToCommentResponse(updatedComment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
        notifyCommentDeleted(id);
    }

    public List<CommentResponse> getCommentsByRecipeId(Long recipeId) {
        List<CommentEntity> comments = commentRepository.findByRecipeId(recipeId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    private void notifyCommentAdded(CommentEntity comment) {
        messagingTemplate.convertAndSend("/topic/comments/" + comment.getRecipeId(), mapToCommentResponse(comment));
    }

    private void notifyCommentDeleted(Long commentId) {
        messagingTemplate.convertAndSend("/topic/comment-deleted", commentId);
    }

    private CommentEntity mapToComment(CommentRequest request) {
        CommentEntity comment = new CommentEntity();
        comment.setUserId(request.getUserId());
        comment.setRecipeId(request.getRecipeId());
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        return comment;
    }

    private CommentResponse mapToCommentResponse(CommentEntity comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getUserId());
        response.setRecipeId(comment.getRecipeId());
        response.setContent(comment.getContent());
        response.setParentId(comment.getParentId());
        return response;
    }
}
