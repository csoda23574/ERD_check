package org.chefcrew.recipe.dto.response;

import org.chefcrew.recipe.domain.Recipe;

public record GetRecipeDataResponse(
        Recipe recipe
) {
}
