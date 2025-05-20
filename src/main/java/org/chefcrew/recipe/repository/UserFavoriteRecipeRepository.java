package org.chefcrew.recipe.repository;

import org.chefcrew.recipe.entity.UserFavoriteRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRecipeRepository extends JpaRepository<UserFavoriteRecipe, Long> {

    Optional<UserFavoriteRecipe> findByUserIdAndExternalRecipeId(Long userId, String externalRecipeId);

    List<UserFavoriteRecipe> findByUserIdOrderByFavoritedAtDesc(Long userId);

    // userId와 externalRecipeId로 삭제
    void deleteByUserIdAndExternalRecipeId(Long userId, String externalRecipeId);
    
    // UserFavoriteRecipe의 PK (id)와 userId로 삭제
    void deleteByIdAndUserId(Long id, Long userId);
}
