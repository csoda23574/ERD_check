package org.chefcrew.recipe.Facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chefcrew.common.validate.Validation;
import org.chefcrew.recipe.dto.request.PostFavoriteRecipeRequest;
import org.chefcrew.recipe.dto.request.PostUsedRecipeRequest;
import org.chefcrew.recipe.dto.response.GetUsedRecipeListResponse;
import org.chefcrew.recipe.dto.response.UserFavoriteRecipeResponse; // DTO 임포트 추가
import org.chefcrew.recipe.entity.OwnRecipe;
import org.chefcrew.recipe.entity.SavedRecipeInfo;
// import org.chefcrew.recipe.entity.UserFavoriteRecipe; // 더 이상 직접 사용하지 않음
import org.chefcrew.recipe.service.OwnRecipeService;
import org.chefcrew.recipe.service.SavedRecipeInfoService;
import org.chefcrew.recipe.service.UserFavoriteRecipeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional // 클래스 레벨 트랜잭션은 유지하거나, 각 메소드에 필요에 따라 설정
public class RecipeFacade {
    private final SavedRecipeInfoService savedRecipeInfoService;
    private final OwnRecipeService ownRecipeService;
    private final UserFavoriteRecipeService userFavoriteRecipeService;
    private final Validation validation;

    @Transactional
    public void postAsUsedRecipe(long userId, PostUsedRecipeRequest postUsedRecipeRequest) {
        log.info("[RecipeFacade] postAsUsedRecipe 시작 - userId: {}, requestBody: {}", userId, postUsedRecipeRequest);
        validation.isExistUserByUserId(userId);

        // userId와 externalRecipeId로 SavedRecipeInfo 조회
        SavedRecipeInfo savedRecipeInfo = savedRecipeInfoService.findByUserIdAndExternalRecipeId(userId, postUsedRecipeRequest.rcpSeq());

        if (savedRecipeInfo == null) {
            savedRecipeInfo = new SavedRecipeInfo();
            savedRecipeInfo.setUserId(userId); // userId 설정
            savedRecipeInfo.setExternalRecipeId(postUsedRecipeRequest.rcpSeq());
            savedRecipeInfo.setRecipeName(postUsedRecipeRequest.title());
            savedRecipeInfo.setAttFileNoMk(postUsedRecipeRequest.thumbnail());
            savedRecipeInfo.setIngredient(postUsedRecipeRequest.ingredients());
            savedRecipeInfo.setCookingProcess(postUsedRecipeRequest.steps());
            // 영양 정보 설정
            savedRecipeInfo.setCalorie(postUsedRecipeRequest.calorie());
            savedRecipeInfo.setCarbohydrate(postUsedRecipeRequest.carbohydrate());
            savedRecipeInfo.setProtein(postUsedRecipeRequest.protein());
            savedRecipeInfo.setFat(postUsedRecipeRequest.fat());
            savedRecipeInfo.setNatrium(postUsedRecipeRequest.natrium());
        }
        
        savedRecipeInfo.updateLastUsedAt(); // 최근 사용 시간 업데이트
        log.info("[RecipeFacade] SavedRecipeInfo 저장/업데이트 시도 전: {}", savedRecipeInfo);
        
        savedRecipeInfoService.saveOrUpdate(savedRecipeInfo);
        log.info("[RecipeFacade] SavedRecipeInfo 저장/업데이트 시도 후. ID: {}", savedRecipeInfo.getId());

        // "요리 완료"로 표시된 경우 OwnRecipe 생성
        if (Boolean.TRUE.equals(postUsedRecipeRequest.cooked()) && savedRecipeInfo.getId() != 0) {
            OwnRecipe ownRecipe = ownRecipeService.getOwnRecipeByUserIdAndRecipeId(userId, savedRecipeInfo.getId());
            if (ownRecipe == null) {
                ownRecipeService.createOwnRecipe(userId, savedRecipeInfo.getId());
                log.info("[RecipeFacade] OwnRecipe 생성 - userId: {}, recipeId: {}", userId, savedRecipeInfo.getId());
            }
        }
        log.info("[RecipeFacade] postAsUsedRecipe 종료");
    }

    @Transactional(readOnly = true)
    public GetUsedRecipeListResponse getUsedRecipeList(long userId) {
        validation.isExistUserByUserId(userId);
        // SavedRecipeInfoService를 사용하여 userId로 최근 본 레시피 목록을 직접 조회
        List<SavedRecipeInfo> recentlyUsedRecipes = savedRecipeInfoService.getRecentlyViewedRecipesByUserId(userId);
        // limit은 서비스 또는 레포지토리 레벨에서 처리 (예: Pageable 사용 또는 쿼리 자체에서 LIMIT)
        // 여기서는 서비스가 이미 적절한 수의 결과를 반환한다고 가정 (예: 상위 20개)

        for (SavedRecipeInfo recipe : recentlyUsedRecipes) {
            Hibernate.initialize(recipe.getIngredient());
            Hibernate.initialize(recipe.getCookingProcess());
            Hibernate.initialize(recipe.getProcessImage());
        }

        return new GetUsedRecipeListResponse(recentlyUsedRecipes);
    }

    @Transactional
    public UserFavoriteRecipeResponse addFavoriteRecipe(long userId, PostFavoriteRecipeRequest requestBody) { // 반환 타입 변경
        validation.isExistUserByUserId(userId);
        // UserFavoriteRecipeService의 DTO 반환 메소드 호출
        return userFavoriteRecipeService.addFavorite(userId, requestBody);
    }

    @Transactional
    public void removeFavoriteRecipe(long userId, long favoriteRecipeId) { 
        validation.isExistUserByUserId(userId);
        // UserFavoriteRecipeService의 PK 기반 삭제 메소드 호출 (변경 없음)
        userFavoriteRecipeService.removeFavoriteById(favoriteRecipeId);
    }

    @Transactional(readOnly = true)
    public List<UserFavoriteRecipeResponse> getFavoriteRecipes(long userId) { // 반환 타입 변경
        validation.isExistUserByUserId(userId);
        // UserFavoriteRecipeService의 DTO 리스트 반환 메소드 호출
        return userFavoriteRecipeService.getFavoritesByUserId(userId);
    }
}
