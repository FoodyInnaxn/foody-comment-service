package com.foody.commentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    @NotNull(message = "UserId has to be present")
    private Long userId;
    @NotNull(message = "RecipeId has to be present")
    private Long recipeId;
    @NotBlank(message = "Content has to be present")
    private String content;
    @NotNull(message = "Date has to be present")
    private Date postedAt;
    private Long parentId;
}
