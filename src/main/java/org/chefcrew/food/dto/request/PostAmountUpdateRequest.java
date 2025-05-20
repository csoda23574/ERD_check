package org.chefcrew.food.dto.request;

import java.util.List;
import org.chefcrew.food.dto.FoodData;

public record PostAmountUpdateRequest(
        List<FoodData> foodDataList
) {
}
