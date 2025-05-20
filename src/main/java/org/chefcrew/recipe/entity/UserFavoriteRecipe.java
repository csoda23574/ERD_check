package org.chefcrew.recipe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_favorite_recipe")
public class UserFavoriteRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    private String externalRecipeId;

    @Column
    private String recipeName;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_favorite_recipe_ingredients", joinColumns = @JoinColumn(name = "user_favorite_recipe_id"))
    @Column(name = "ingredient", length = 2000)
    private List<String> ingredient;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_favorite_recipe_cooking_process", joinColumns = @JoinColumn(name = "user_favorite_recipe_id"))
    @Column(name = "cooking_step", length = 2000)
    private List<String> cookingProcess;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_favorite_recipe_process_images", joinColumns = @JoinColumn(name = "user_favorite_recipe_id"))
    @Column(name = "process_image_url", length = 500)
    private List<String> processImage;

    @Column
    private String attFileNoMk; // 썸네일 (레시피 대표 이미지)

    @Column
    private Float calorie;

    @Column
    private Float natrium;

    @Column
    private Float fat;

    @Column
    private Float protein;

    @Column
    private Float carbohydrate;

    @Column(nullable = false)
    private LocalDateTime favoritedAt;

    // 생성자, getter, setter 등은 Lombok이 처리
}
