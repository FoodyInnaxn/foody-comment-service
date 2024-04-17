package com.foody.commentservice.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
