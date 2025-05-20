package org.chefcrew.recipe.dto.response;

import org.chefcrew.recipe.entity.SavedRecipeInfo;

import java.util.List;

public record GetUsedRecipeListResponse(
        List<SavedRecipeInfo> recipeList
) {
}
