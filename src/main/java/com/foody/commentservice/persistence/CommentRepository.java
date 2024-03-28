package com.foody.commentservice.persistence;

import com.foody.commentservice.persistence.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByRecipeId(Long recipeId);
}
