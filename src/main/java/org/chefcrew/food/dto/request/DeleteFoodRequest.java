package org.chefcrew.food.dto.request;

import java.util.List;

public record DeleteFoodRequest(
        List<String> foodNameList
) {
}
