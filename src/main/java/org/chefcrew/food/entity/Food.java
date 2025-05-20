package org.chefcrew.food.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class Food {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long foodId;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "quantity")
    private float amount;

    @Column(name = "unit")
    private String unit;

    public Food(String name, long userId, float amount, String unit) {
        this.foodName = name;
        this.userId = userId;
        this.amount = amount;
        this.unit = unit;
    }

    public Food(String name, long userId, float amount) {
        this.foodName = name;
        this.userId = userId;
        this.amount = amount;
    }

    public Food(String name, long userId) {
        this.foodName = name;
        this.userId = userId;
    }

    public String getUnit() {
        return unit;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void updateAmountAndUnit(float amount, String unit) {
        this.amount = amount;
        this.unit = unit;
    }
}