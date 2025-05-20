package org.chefcrew.food.dto.response;

import java.util.List;

public record GetOwnFoodResponse(
        List<FoodSimpleResponse> foodList
) {
}
