package com.foody.commentservice.business;

import com.foody.commentservice.business.exceptions.CommentNotFoundException;
import com.foody.commentservice.business.exceptions.MissingFieldsException;
import com.foody.commentservice.configuration.RabbitMQConfig;
import com.foody.commentservice.dto.CommentRequest;
import com.foody.commentservice.dto.CommentResponse;
import com.foody.commentservice.dto.CommentsResponse;
import com.foody.commentservice.persistence.CommentRepository;
import com.foody.commentservice.persistence.entity.CommentEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentResponse createComment(@Valid CommentRequest request, Long userId) {
        validateRequest(request, userId);

        CommentEntity comment = mapToComment(request, userId);
        CommentEntity savedComment = commentRepository.save(comment);
        return mapToCommentResponse(savedComment);
    }

    public CommentResponse updateComment(Long userId, Long commentId, @Valid CommentRequest request) {
        validateRequest(request, userId);

        CommentEntity existingComment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        existingComment.setUserId(userId);
        existingComment.setRecipeId(request.getRecipeId());
        existingComment.setContent(request.getContent());
        existingComment.setPostedAt(request.getPostedAt());
        existingComment.setParentId(request.getParentId());

        CommentEntity updatedComment = commentRepository.save(existingComment);
        return mapToCommentResponse(updatedComment);
    }


    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public CommentsResponse getCommentsByRecipeId(Long recipeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentEntity> commentPage = commentRepository.findTopLevelCommentsByRecipeId(recipeId, pageable);

        if (commentPage.isEmpty()) {
            throw new CommentNotFoundException();
        }

        List<CommentResponse> commentResponse = commentPage.getContent().stream()
                .map(this::mapCommentWithNested)
                .toList();

        if (commentResponse.isEmpty()) {
            throw new CommentNotFoundException();
        }

        return new CommentsResponse(commentResponse, commentPage.getTotalPages());
    }


    private CommentResponse mapCommentWithNested(CommentEntity topLevelComment) {
        CommentResponse commentResponse = mapToCommentResponse(topLevelComment);

        List<CommentEntity> nestedComments = commentRepository.findNestedCommentsByRecipeIdAndParentId(
                topLevelComment.getRecipeId(),
                topLevelComment.getId()
        );

        List<CommentResponse> nestedCommentResponses = nestedComments.stream()
                .map(this::mapCommentWithNested)
                .collect(Collectors.toList());

        commentResponse.setNestedComments(nestedCommentResponses);

        return commentResponse;
    }

    private CommentEntity mapToComment(CommentRequest request, Long userId) {
        CommentEntity comment = new CommentEntity();
        comment.setUserId(userId);
        comment.setRecipeId(request.getRecipeId());
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setPostedAt(request.getPostedAt());
        return comment;
    }

    private CommentResponse mapToCommentResponse(CommentEntity comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getUserId());
        response.setRecipeId(comment.getRecipeId());
        response.setContent(comment.getContent());
        response.setPostedAt(comment.getPostedAt());
        response.setParentId(comment.getParentId());
        return response;
    }

    private void validateRequest(CommentRequest request, Long userId) {
        if (userId == null || request.getRecipeId() == null || request.getPostedAt() == null) {
            throw new MissingFieldsException("userId, recipeId, and postedAt must not be null");
        }
    }

    @RabbitListener(queues = RabbitMQConfig.FANOUT_COMMENT_QUEUE)
    @Transactional
    public void handleDelete(Long recipeId) {
        List<CommentEntity> commentEntities = commentRepository.findByRecipeId(recipeId);
        if(!commentEntities.isEmpty()){
            commentRepository.deleteByRecipeId(recipeId);
        }
    }
}
