package org.chefcrew.recipe.repository;

import static org.chefcrew.recipe.entity.QOwnRecipe.ownRecipe;

import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j; // Slf4j import 추가
import org.chefcrew.recipe.entity.OwnRecipe;
import org.springframework.stereotype.Repository;

@Slf4j // Slf4j 어노테이션 추가
@Repository
public class OwnRecipeRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public OwnRecipeRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void saveOwnData(OwnRecipe ownRecipe) {
        log.info("[OwnRecipeRepository] saveOwnData 시작 - ownRecipe to persist: userId={}, recipeId={}", ownRecipe != null ? ownRecipe.getUserId() : "null", ownRecipe != null ? ownRecipe.getRecipeId() : "null");
        try {
            if (ownRecipe == null) {
                log.error("[OwnRecipeRepository] 저장하려는 ownRecipe 객체가 null입니다.");
                // 필요시 예외를 던지거나 다른 방식으로 처리합니다.
                // throw new IllegalArgumentException("Cannot save a null OwnRecipe entity.");
                return; 
            }
            // persist 하기 전 ID는 0 또는 할당되지 않은 상태여야 합니다 (새 엔티티의 경우).
            log.info("[OwnRecipeRepository] em.persist 시도 전: ownRecipe (ID before persist: {})", ownRecipe.getId());
            em.persist(ownRecipe);
            // persist 호출 후에는 ownRecipe 객체에 ID가 할당됩니다 (데이터베이스에 따라 즉시 또는 flush 시점에).
            log.info("[OwnRecipeRepository] em.persist 호출 후. ownRecipe ID (after persist attempt, may require flush to be visible): {}", ownRecipe.getId());
        } catch (Exception e) {
            log.error("[OwnRecipeRepository] saveOwnData 중 예외 발생: {}", e.getMessage(), e);
            throw e; // 예외를 다시 던져 트랜잭션 롤백이 정상적으로 이루어지도록 함
        }
        log.info("[OwnRecipeRepository] saveOwnData 종료");
    }

    public List<OwnRecipe> findAllByUserId(long userId) {
        return query.selectFrom(ownRecipe)
                .where(ownRecipe.userId.eq(userId))
                .fetch()
                .stream().toList();
    }

    public OwnRecipe findByUserIdAndRecipeId(long userId, long recipeId) {
        return query.selectFrom(ownRecipe)
                .where(ownRecipe.userId.eq(userId).and(ownRecipe.recipeId.eq(recipeId)))
                .fetchFirst(); // fetchOne()에서 변경됨
    }

    public void deleteAllById(List<Long> idList) {
        new JPADeleteClause(em, ownRecipe)
                .where(ownRecipe.id.in(idList))
                .execute();
        em.flush();
    }
}
