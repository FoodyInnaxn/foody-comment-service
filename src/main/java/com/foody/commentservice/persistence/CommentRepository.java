package com.foody.commentservice.persistence;

import com.foody.commentservice.persistence.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
//    @Query("SELECT c FROM CommentEntity c WHERE c.recipeId = :recipeId AND c.parentId IS NULL")
//    List<CommentEntity> findTopLevelCommentsByRecipeId(@Param("recipeId") Long recipeId);
    @Query("SELECT c FROM CommentEntity c WHERE c.recipeId = :recipeId AND c.parentId IS NULL")
    Page<CommentEntity> findTopLevelCommentsByRecipeId(@Param("recipeId") Long recipeId, Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.recipeId = :recipeId AND c.parentId = :parentId")
    List<CommentEntity> findNestedCommentsByRecipeIdAndParentId(@Param("recipeId") Long recipeId, @Param("parentId") Long parentId);

    List<CommentEntity> findByRecipeId(Long id);
    void deleteByRecipeId (Long id);
}
