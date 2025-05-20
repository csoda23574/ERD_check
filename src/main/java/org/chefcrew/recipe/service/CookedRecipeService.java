package org.chefcrew.recipe.service;

import lombok.RequiredArgsConstructor;
import org.chefcrew.recipe.entity.SavedRecipeInfo;
import org.chefcrew.recipe.repository.SavedRecipeInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 일반적으로 조회 서비스는 readOnly = true 설정
@RequiredArgsConstructor
public class CookedRecipeService {

    private final SavedRecipeInfoRepository savedRecipeInfoRepository;

    public List<SavedRecipeInfo> getRecentlyUsedRecipes(List<Long> recipeIds, int limit) {
        // recipeIds가 비어있으면 빈 리스트 반환
        if (recipeIds == null || recipeIds.isEmpty()) {
            return List.of();
        }
        return savedRecipeInfoRepository.findByIdsOrderByLastUsedAtDesc(recipeIds, limit);
    }
}
