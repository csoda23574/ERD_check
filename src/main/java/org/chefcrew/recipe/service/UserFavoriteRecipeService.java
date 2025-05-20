package org.chefcrew.recipe.service;

import lombok.RequiredArgsConstructor;
import org.chefcrew.recipe.dto.request.PostFavoriteRecipeRequest;
import org.chefcrew.recipe.dto.response.UserFavoriteRecipeResponse; // DTO 임포트 변경
import org.chefcrew.recipe.entity.UserFavoriteRecipe;
import org.chefcrew.recipe.repository.UserFavoriteRecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.hibernate.Hibernate; // Hibernate.initialize 사용 시 필요

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Collectors 임포트 추가

@Service
@RequiredArgsConstructor
public class UserFavoriteRecipeService {

    private final UserFavoriteRecipeRepository userFavoriteRecipeRepository;

    private Float parseFloatSafe(String value) {
        if (value == null || value.isEmpty() || value.equalsIgnoreCase("정보 없음") || value.equalsIgnoreCase("null")) {
            return null;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            System.err.println("Could not parse '" + value + "' to Float, returning null. Error: " + e.getMessage());
            return null;
        }
    }

    @Transactional
    public UserFavoriteRecipeResponse addFavorite(Long userId, PostFavoriteRecipeRequest request) { // 반환 타입 변경
        Optional<UserFavoriteRecipe> existingFavorite = userFavoriteRecipeRepository.findByUserIdAndExternalRecipeId(userId, request.getRcpSeq());
        if (existingFavorite.isPresent()) {
            // 이미 존재하면 DTO로 변환하여 반환
            return UserFavoriteRecipeResponse.fromEntity(existingFavorite.get());
        }

        UserFavoriteRecipe newFavorite = new UserFavoriteRecipe();
        newFavorite.setUserId(userId);
        newFavorite.setExternalRecipeId(request.getRcpSeq());
        newFavorite.setRecipeName(request.getTitle());
        newFavorite.setIngredient(request.getIngredients());
        newFavorite.setCookingProcess(request.getSteps());
        newFavorite.setAttFileNoMk(request.getThumbnail());

        newFavorite.setCalorie(parseFloatSafe(request.getCalorie()));
        newFavorite.setCarbohydrate(parseFloatSafe(request.getCarbohydrate()));
        newFavorite.setProtein(parseFloatSafe(request.getProtein()));
        newFavorite.setFat(parseFloatSafe(request.getFat()));
        newFavorite.setNatrium(parseFloatSafe(request.getNatrium()));
        
        newFavorite.setFavoritedAt(LocalDateTime.now());

        UserFavoriteRecipe savedEntity = userFavoriteRecipeRepository.save(newFavorite);
        return UserFavoriteRecipeResponse.fromEntity(savedEntity); // DTO로 변환하여 반환
    }

    @Transactional
    public void removeFavoriteByExternalId(Long userId, String externalRecipeId) {
        userFavoriteRecipeRepository.deleteByUserIdAndExternalRecipeId(userId, externalRecipeId);
    }
    
    @Transactional
    public void removeFavoriteById(Long favoriteId) { 
        userFavoriteRecipeRepository.deleteById(favoriteId);
    }

    @Transactional(readOnly = true)
    public List<UserFavoriteRecipeResponse> getFavoritesByUserId(Long userId) { // 반환 타입 변경
        List<UserFavoriteRecipe> favoriteEntities = userFavoriteRecipeRepository.findByUserIdOrderByFavoritedAtDesc(userId);
        // 엔티티 리스트를 DTO 리스트로 변환
        return UserFavoriteRecipeResponse.fromEntityList(favoriteEntities);
    }

    @Transactional(readOnly = true)
    public Optional<UserFavoriteRecipeResponse> findByUserIdAndExternalRecipeIdDto(Long userId, String externalRecipeId) { // 메소드명 변경 및 반환타입 변경
        return userFavoriteRecipeRepository.findByUserIdAndExternalRecipeId(userId, externalRecipeId)
                                         .map(UserFavoriteRecipeResponse::fromEntity);
    }

    // 기존 엔티티 반환 메소드가 다른 곳에서 사용될 수 있으므로 유지 (필요 없다면 삭제 가능)
    @Transactional(readOnly = true)
    public Optional<UserFavoriteRecipe> findByUserIdAndExternalRecipeId(Long userId, String externalRecipeId) {
        return userFavoriteRecipeRepository.findByUserIdAndExternalRecipeId(userId, externalRecipeId);
    }
}
