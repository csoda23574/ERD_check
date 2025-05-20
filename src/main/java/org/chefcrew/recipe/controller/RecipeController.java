package org.chefcrew.recipe.controller;

import lombok.RequiredArgsConstructor;
import org.chefcrew.recipe.Facade.RecipeFacade;
import org.chefcrew.recipe.dto.request.GetRecipeRequest;
import org.chefcrew.recipe.dto.request.PostUsedRecipeRequest;
import org.chefcrew.recipe.dto.request.PostFavoriteRecipeRequest;
import org.chefcrew.recipe.dto.response.GetRecipeResponse;
import org.chefcrew.recipe.dto.response.GetUsedRecipeListResponse;
import org.chefcrew.recipe.dto.response.UserFavoriteRecipeResponse; // DTO 임포트 추가
import org.chefcrew.recipe.service.RecipeService;
import org.chefcrew.recipe.entity.SavedRecipeInfo;
// import org.chefcrew.recipe.entity.UserFavoriteRecipe; // 더 이상 직접 사용하지 않음
import org.chefcrew.recipe.service.SavedRecipeInfoService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeFacade recipeFacade;
    private final SavedRecipeInfoService savedRecipeInfoService;

    @PostMapping
    public ResponseEntity<GetRecipeResponse> getRecommendRecipes(@RequestBody GetRecipeRequest requestBody) {
        return ResponseEntity.ok().body(recipeService.getRecommendRecipe(requestBody, requestBody.exactMatch()));
    }

    @PostMapping("/used")
    public ResponseEntity<Void> postAsUsedRecipe(@RequestBody PostUsedRecipeRequest requestBody) {
        Long userId = 1L; // TODO: 인증된 userId 추출 필요
        recipeFacade.postAsUsedRecipe(userId, requestBody);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/used/{userId}")
    public ResponseEntity<GetUsedRecipeListResponse> getUsedRecipeList(@PathVariable Long userId) {
        return ResponseEntity.ok(recipeFacade.getUsedRecipeList(userId));
    }

    @GetMapping("/detail/{recipeId}")
    public ResponseEntity<SavedRecipeInfo> getSavedRecipeDetail(@PathVariable Long recipeId) {
        SavedRecipeInfo savedRecipeInfo = savedRecipeInfoService.getSavedRecipeInfo(recipeId);
        if (savedRecipeInfo != null) {
            return ResponseEntity.ok(savedRecipeInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/favorite")
    public ResponseEntity<UserFavoriteRecipeResponse> addFavoriteRecipe(@RequestParam Long userId, @RequestBody PostFavoriteRecipeRequest requestBody) {
        UserFavoriteRecipeResponse favoriteRecipeResponse = recipeFacade.addFavoriteRecipe(userId, requestBody);
        return ResponseEntity.ok(favoriteRecipeResponse);
    }

    @DeleteMapping("/favorite")
    public ResponseEntity<Void> removeFavoriteRecipe(@RequestParam Long userId, @RequestParam Long recipeId) {
        recipeFacade.removeFavoriteRecipe(userId, recipeId); 
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorites/{userId}")
    public ResponseEntity<List<UserFavoriteRecipeResponse>> getFavoriteRecipes(@PathVariable Long userId) {
        List<UserFavoriteRecipeResponse> favoriteRecipes = recipeFacade.getFavoriteRecipes(userId);
        return ResponseEntity.ok(favoriteRecipes);
    }
}
