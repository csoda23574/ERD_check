package org.chefcrew.recipe.entity;

import org.chefcrew.recipe.enums.NationOption;

public record BasicRecipeInfo(
        long recipeId,
        String recipeName,
        String recipeSumry,
        NationOption nationOption
) {
}
