package com.foody.commentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private Long userId;
    private Long recipeId;
    private String content;
    private Date postedAt;
    private Long parentId;
    private List<CommentResponse> nestedComments; // Add this field for nested comments
}
