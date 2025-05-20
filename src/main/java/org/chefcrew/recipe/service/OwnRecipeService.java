package org.chefcrew.recipe.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Slf4j import 추가
import org.chefcrew.recipe.entity.OwnRecipe;
import org.chefcrew.recipe.repository.OwnRecipeRepository;
import org.chefcrew.user.entity.User;
import org.springframework.stereotype.Service;

@Slf4j // Slf4j 어노테이션 추가
@Service
@RequiredArgsConstructor
public class OwnRecipeService {
    private final OwnRecipeRepository ownRecipeRepository;

    //유저 id로 조회하는 메서드
    public List<OwnRecipe> getOwnRecipeListByUserId(long userId) {
        return ownRecipeRepository.findAllByUserId(userId);
    }

    public OwnRecipe getOwnRecipeByUserIdAndRecipeId(long userId, long recipeId) {
        log.info("[OwnRecipeService] getOwnRecipeByUserIdAndRecipeId 호출 - userId: {}, recipeId: {}", userId, recipeId);
        OwnRecipe ownRecipe = ownRecipeRepository.findByUserIdAndRecipeId(userId, recipeId);
        log.info("[OwnRecipeService] getOwnRecipeByUserIdAndRecipeId 결과: {}", ownRecipe);
        return ownRecipe;
    }

    // OwnRecipe 생성 메서드 추가
    public OwnRecipe createOwnRecipe(long userId, long recipeId) {
        log.info("[OwnRecipeService] createOwnRecipe 시작 - userId: {}, recipeId: {}", userId, recipeId);
        if (recipeId == 0) {
            log.warn("[OwnRecipeService] recipeId가 0입니다. OwnRecipe를 생성할 수 없습니다. savedRecipeInfo.id가 제대로 전달되지 않았을 수 있습니다.");
            // 프로덕션 코드에서는 여기서 예외를 발생시키거나 다른 방식으로 처리하는 것이 좋습니다.
            // throw new IllegalArgumentException("Recipe ID cannot be 0 when creating OwnRecipe. Check if SavedRecipeInfo was persisted correctly.");
            return null; // 또는 예외 발생
        }
        OwnRecipe ownRecipe = new OwnRecipe(userId, recipeId);
        log.info("[OwnRecipeService] OwnRecipe 객체 생성: userId={}, recipeId={}", ownRecipe.getUserId(), ownRecipe.getRecipeId());
        ownRecipeRepository.saveOwnData(ownRecipe);
        log.info("[OwnRecipeService] ownRecipeRepository.saveOwnData 호출 후");
        // ownRecipe 객체는 persist 이후에도 ID 필드가 채워지지 않을 수 있습니다 (saveOwnData가 반환값이 없고, persist는 ID를 객체에 즉시 반영하지 않을 수 있음).
        // ID가 필요한 경우, 저장 후 다시 조회하거나 saveOwnData가 저장된 엔티티를 반환하도록 수정해야 합니다.
        log.info("[OwnRecipeService] createOwnRecipe 종료 - 반환 전 ownRecipe (ID는 아직 없을 수 있음): userId={}, recipeId={}", ownRecipe.getUserId(), ownRecipe.getRecipeId());
        return ownRecipe;
    }

    //저장
    public void saveOwnData(OwnRecipe ownRecipe) {
        log.info("[OwnRecipeService] saveOwnData 호출됨 (실제 저장은 Repository에서): userId={}, recipeId={}", ownRecipe != null ? ownRecipe.getUserId() : "null", ownRecipe != null ? ownRecipe.getRecipeId() : "null");
        ownRecipeRepository.saveOwnData(ownRecipe);
        log.info("[OwnRecipeService] ownRecipeRepository.saveOwnData 호출 완료");
    }

    public void deleteAllByUser(User user) {
        List<OwnRecipe> foods = ownRecipeRepository.findAllByUserId(user.getUserId());
        ownRecipeRepository.deleteAllById(foods.stream().map(
                (OwnRecipe::getId)).collect(Collectors.toList()));
    }
}