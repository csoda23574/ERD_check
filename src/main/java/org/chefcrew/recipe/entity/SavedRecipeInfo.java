package org.chefcrew.recipe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Slf4j
@NoArgsConstructor
@Getter
@Setter
public class SavedRecipeInfo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = true)
    private Long userId; // 사용자 ID 필드 추가

    @Column(nullable = true) // unique = true 제약 조건 제거
    private String externalRecipeId; // 외부 레시피 ID (예: 공공 API의 RCP_SEQ), String으로 변경

    String recipeName;

    // recipeImage 필드 제거 (attFileNoMain에 해당). thumbnail은 attFileNoMk에 저장됨

    // 상세 정보 필드 추가
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "saved_recipe_ingredients", joinColumns = @JoinColumn(name = "saved_recipe_id"))
    @Column(name = "ingredient", length = 2000)
    private List<String> ingredient;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "saved_recipe_cooking_process", joinColumns = @JoinColumn(name = "saved_recipe_id"))
    @Column(name = "cooking_step", length = 2000)
    private List<String> cookingProcess;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "saved_recipe_process_images", joinColumns = @JoinColumn(name = "saved_recipe_id"))
    @Column(name = "process_image_url", length = 500)
    private List<String> processImage;

    private Float calorie;
    private Float natrium; // 나트륨
    private Float fat;     // 지방
    private Float protein; // 단백질 (Recipe.java의 protien 오타 수정 반영)
    private Float carbohydrate; // 탄수화물
    private String attFileNoMk; // 레시피 대표 이미지 (썸네일)

    @Column(nullable = false)
    private boolean isFavorite = false;

    private LocalDateTime favoritedAt;

    private LocalDateTime lastUsedAt;

    // 기존 생성자 제거
    // public SavedRecipeInfo(String recipeName, String recipeImage){
    // this.recipeName = recipeName;
    // this.recipeImage = recipeImage; // recipeImage 필드 제거로 인해 이 생성자는 유효하지 않음
    // }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
        if (favorite) {
            this.favoritedAt = LocalDateTime.now();
        } else {
            this.favoritedAt = null;
        }
    }

    public void updateLastUsedAt() {
        this.lastUsedAt = LocalDateTime.now();
    }
}
