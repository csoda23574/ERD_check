package org.chefcrew.food.dto.request;

import java.util.List;

public record AddFoodRequest(
        List<String> foodNameList,
        List<String> quantityList,
        List<String> unitList
) {
}
