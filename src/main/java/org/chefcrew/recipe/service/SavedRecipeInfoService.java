package org.chefcrew.recipe.service;

// import jakarta.transaction.Transactional; // 기존 jakarta.transaction.Transactional 주석 처리
import org.springframework.transaction.annotation.Transactional; // Spring의 Transactional 사용
import lombok.RequiredArgsConstructor;
import org.chefcrew.recipe.entity.SavedRecipeInfo;
import org.chefcrew.recipe.repository.SavedRecipeInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
// @Transactional // 클래스 레벨 Transactional은 메서드 레벨에서 필요에 따라 오버라이드 가능
@RequiredArgsConstructor
public class SavedRecipeInfoService {
    public final SavedRecipeInfoRepository savedRecipeInfoRepository;

    @Transactional(readOnly = true)
    public SavedRecipeInfo getMenuDataFromDBByName(String recipeName) {
        if (savedRecipeInfoRepository.existsByRecipeName(recipeName))
            return savedRecipeInfoRepository.findByRecipeName(recipeName);
        return null;
    }

    @Transactional
    public void postAsUsedRecipe(SavedRecipeInfo savedRecipeInfo) {
        // ID 존재 여부 등으로 새 엔티티인지 기존 엔티티인지 판단하여
        // em.persist() 또는 em.merge()를 호출하는 로직이 Repository의 saveRecipe에 구현되어 있다고 가정
        savedRecipeInfoRepository.saveRecipe(savedRecipeInfo);
    }

    @Transactional(readOnly = true)
    public SavedRecipeInfo getSavedRecipeInfo(Long recipeId) {
        return savedRecipeInfoRepository.findById(recipeId);
    }

    // externalRecipeId로 SavedRecipeInfo 조회하는 메서드 추가
    @Transactional(readOnly = true)
    public SavedRecipeInfo findByExternalRecipeId(String externalRecipeId) { // 이름 변경 및 Repository 호출 명확화
        return savedRecipeInfoRepository.findByExternalRecipeId(externalRecipeId);
    }

    // userId와 externalRecipeId로 SavedRecipeInfo 조회하는 메서드 추가
    @Transactional(readOnly = true)
    public SavedRecipeInfo findByUserIdAndExternalRecipeId(Long userId, String externalRecipeId) {
        return savedRecipeInfoRepository.findByUserIdAndExternalRecipeId(userId, externalRecipeId);
    }

    @Transactional
    public SavedRecipeInfo saveOrUpdate(SavedRecipeInfo savedRecipeInfo) { // 새로운 saveOrUpdate 메소드
        savedRecipeInfoRepository.saveRecipe(savedRecipeInfo);
        return savedRecipeInfo;
    }

    @Transactional(readOnly = true)
    public List<SavedRecipeInfo> getRecipeInfoList(List<Long> recipesIdList) {
        if (recipesIdList == null || recipesIdList.isEmpty()) {
            return List.of();
        }
        return recipesIdList.stream()
                .map(savedRecipeInfoRepository::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 즐겨찾기 추가
    @Transactional
    public SavedRecipeInfo addFavoriteRecipe(Long recipeId) {
        SavedRecipeInfo recipeInfo = savedRecipeInfoRepository.findById(recipeId);
        if (recipeInfo != null) {
            recipeInfo.setFavorite(true); // setFavorite 내부에서 favoritedAt 업데이트
            // savedRecipeInfoRepository.saveRecipe(recipeInfo); // 변경 감지로 업데이트되므로 명시적 호출 불필요할 수 있음
        } else {
            // 레시피 정보가 없는 경우 예외 처리 또는 로깅
            // throw new EntityNotFoundException("Recipe not found with id: " + recipeId);
        }
        return recipeInfo; 
    }

    // 즐겨찾기 제거
    @Transactional
    public SavedRecipeInfo removeFavoriteRecipe(Long recipeId) {
        SavedRecipeInfo recipeInfo = savedRecipeInfoRepository.findById(recipeId);
        if (recipeInfo != null) {
            recipeInfo.setFavorite(false); // setFavorite 내부에서 favoritedAt 업데이트 (null로)
            // savedRecipeInfoRepository.saveRecipe(recipeInfo); // 변경 감지로 업데이트
        } else {
            // 레시피 정보가 없는 경우 예외 처리 또는 로깅
        }
        return recipeInfo;
    }

    // ID 목록으로 즐겨찾기된 SavedRecipeInfo 목록 조회
    @Transactional(readOnly = true)
    public List<SavedRecipeInfo> getFavoriteRecipeInfos(List<Long> recipeIds) {
        if (recipeIds == null || recipeIds.isEmpty()) {
            return List.of();
        }
        return savedRecipeInfoRepository.findByIdsAndFavoriteTrueOrderByFavoritedAtDesc(recipeIds);
    }

    // userId로 최근 본 레시피 목록 조회
    @Transactional(readOnly = true)
    public List<SavedRecipeInfo> getRecentlyViewedRecipesByUserId(Long userId) {
        return savedRecipeInfoRepository.findByUserIdOrderByLastUsedAtDesc(userId);
    }
}
