package org.chefcrew.food.repository;

import static org.chefcrew.food.entity.QFood.food;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.chefcrew.food.entity.Food;
import org.springframework.stereotype.Repository;

@Repository
public class FoodRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public FoodRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void saveFood(Food food) {
        em.merge(food); // 신규/수정 모두 반영
    }

    //특정 유저가 소유한 식재료 조회
    public List<Food> findByUserId(long userId) {
        return query.selectFrom(food)
                .where(food.userId.eq(userId))
                .fetch();
    }

    public boolean existsByUserId(long userId){
        Integer count = query.selectOne()
                .from(food)
                .where(food.userId.eq(userId))
                .fetchFirst();
        return count != null && count > 0;
    }

    public void deleteFood(long userId, List<String> foodNameList) {
        new JPADeleteClause(em, food)
                .where(food.foodName.in(foodNameList)
                        .and(food.userId.eq(userId)))
                .execute();
    }

    public boolean existsByFoodNameAndUserId(String foodName, long userId) {
        Integer count = query.selectOne()
                .from(food)
                .where(food.foodName.eq(foodName)
                        .and(food.userId.eq(userId)))
                .fetchFirst();
        return count != null && count > 0;

    }

    public Food findByFoodNameAndUserId(String foodName, long userId) {
        return query.selectFrom(food)
                .where(food.foodName.eq(foodName)
                        .and(food.userId.eq(userId)))
                .fetchOne();
    }

    public Food findById(long foodId) {
        return query.selectFrom(food)
                .where(food.foodId.eq(foodId))
                .fetchOne();
    }
}