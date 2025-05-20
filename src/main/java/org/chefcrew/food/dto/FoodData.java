package org.chefcrew.food.dto;

import lombok.Getter;

@Getter
public class FoodData {
    private long foodId;
    private float amount;
    private String unit;
    private String foodName; // foodName 필드 추가
}
