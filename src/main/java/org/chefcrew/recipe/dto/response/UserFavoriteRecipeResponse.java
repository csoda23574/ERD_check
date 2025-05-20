package org.chefcrew.recipe.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.chefcrew.recipe.entity.UserFavoriteRecipe; // 엔티티 임포트

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections; // Collections 임포트 추가

@Getter
@Setter
public class UserFavoriteRecipeResponse {
    private Long id;
    private Long userId;
    private String externalRecipeId;
    private String recipeName; // UserFavoriteRecipe 엔티티의 title 필드에 해당
    private List<String> ingredient;
    private List<String> cookingProcess;
    // private List<String> processImage; // 필요 시 주석 해제
    private String attFileNoMk; // UserFavoriteRecipe 엔티티의 thumbnail 필드에 해당
    private Float calorie;
    private Float natrium;
    private Float fat;
    private Float protein;
    private Float carbohydrate;
    private LocalDateTime favoritedAt;

    // UserFavoriteRecipe 엔티티를 UserFavoriteRecipeResponse DTO로 변환하는 정적 메소드
    public static UserFavoriteRecipeResponse fromEntity(UserFavoriteRecipe entity) {
        if (entity == null) {
            return null;
        }
        UserFavoriteRecipeResponse dto = new UserFavoriteRecipeResponse();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setExternalRecipeId(entity.getExternalRecipeId());
        dto.setRecipeName(entity.getRecipeName()); // 엔티티의 recipeName 사용 (이전에 title로 매핑했었음)

        // 지연 로딩된 컬렉션에 안전하게 접근하여 DTO에 설정
        // 컬렉션이 null일 경우 빈 리스트로 초기화
        dto.setIngredient(entity.getIngredient() != null ? new java.util.ArrayList<>(entity.getIngredient()) : Collections.emptyList());
        dto.setCookingProcess(entity.getCookingProcess() != null ? new java.util.ArrayList<>(entity.getCookingProcess()) : Collections.emptyList());
        // dto.setProcessImage(entity.getProcessImage() != null ? new java.util.ArrayList<>(entity.getProcessImage()) : Collections.emptyList()); // 필요 시 주석 해제

        dto.setAttFileNoMk(entity.getAttFileNoMk());
        dto.setCalorie(entity.getCalorie());
        dto.setNatrium(entity.getNatrium());
        dto.setFat(entity.getFat());
        dto.setProtein(entity.getProtein());
        dto.setCarbohydrate(entity.getCarbohydrate());
        dto.setFavoritedAt(entity.getFavoritedAt());
        return dto;
    }

    // List<UserFavoriteRecipe>를 List<UserFavoriteRecipeResponse>로 변환하는 정적 메소드
    public static List<UserFavoriteRecipeResponse> fromEntityList(List<UserFavoriteRecipe> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                       .map(UserFavoriteRecipeResponse::fromEntity)
                       .collect(Collectors.toList());
    }
}
