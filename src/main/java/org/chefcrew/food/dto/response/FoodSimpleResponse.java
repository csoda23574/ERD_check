package org.chefcrew.food.dto.response;

public record FoodSimpleResponse(
    Long id,
    String foodName,
    String quantity,
    String unit
) {}
