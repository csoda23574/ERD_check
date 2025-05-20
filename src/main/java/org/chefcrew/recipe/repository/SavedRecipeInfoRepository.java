package org.chefcrew.recipe.repository;

import static org.chefcrew.recipe.entity.QSavedRecipeInfo.savedRecipeInfo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.chefcrew.recipe.entity.SavedRecipeInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SavedRecipeInfoRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public SavedRecipeInfoRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void saveRecipe(SavedRecipeInfo savedRecipeInfo) {
        // ID가 0이 아니거나, 이미 영속화된 엔티티인지 확인하여 merge 또는 persist 결정
        if (savedRecipeInfo.getId() != 0 && em.contains(savedRecipeInfo)) {
            em.merge(savedRecipeInfo);
        } else if (savedRecipeInfo.getId() != 0 && findById(savedRecipeInfo.getId()) != null) {
             // ID가 있고 DB에 존재하지만 영속성 컨텍스트에는 없는 경우 (준영속 상태)
            em.merge(savedRecipeInfo);
        } else {
            em.persist(savedRecipeInfo);
        }
    }

    public boolean existsByRecipeName(String recipeName) {
        Integer count = query.selectOne()
                .from(savedRecipeInfo)
                .where(savedRecipeInfo.recipeName.eq(recipeName))
                .fetchFirst();
        return count != null && count > 0;
    }

    public SavedRecipeInfo findByRecipeName(String recipeName) {
        return query.selectFrom(savedRecipeInfo)
                .where(savedRecipeInfo.recipeName.eq(recipeName))
                .fetchOne();
    }

    public SavedRecipeInfo findById(long id) {
        return query.selectFrom(savedRecipeInfo)
                .where(savedRecipeInfo.id.eq(id))
                .fetchOne();
    }

    // externalRecipeId로 SavedRecipeInfo 조회하는 메서드 추가
    public SavedRecipeInfo findByExternalRecipeId(String externalRecipeId) { // 파라미터 타입을 String으로 변경
        if (externalRecipeId == null) return null;
        return query.selectFrom(savedRecipeInfo)
                .where(savedRecipeInfo.externalRecipeId.eq(externalRecipeId)) // 이제 타입이 일치합니다.
                .fetchOne();
    }

    // userId로 SavedRecipeInfo 목록 조회 (최근 사용 순)
    public List<SavedRecipeInfo> findByUserIdOrderByLastUsedAtDesc(Long userId) {
        return query.selectFrom(savedRecipeInfo)
                .where(savedRecipeInfo.userId.eq(userId))
                .orderBy(savedRecipeInfo.lastUsedAt.desc())
                .fetch();
    }

    // userId와 externalRecipeId로 SavedRecipeInfo 조회
    public SavedRecipeInfo findByUserIdAndExternalRecipeId(Long userId, String externalRecipeId) {
        return query.selectFrom(savedRecipeInfo)
                .where(savedRecipeInfo.userId.eq(userId)
                        .and(savedRecipeInfo.externalRecipeId.eq(externalRecipeId)))
                .fetchOne();
    }

    public List<SavedRecipeInfo> findByIdsAndFavoriteTrueOrderByFavoritedAtDesc(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return query.selectFrom(savedRecipeInfo)
                .where(savedRecipeInfo.id.in(ids)
                        .and(savedRecipeInfo.isFavorite.isTrue()))
                .orderBy(savedRecipeInfo.favoritedAt.desc())
                .fetch();
    }

    public List<SavedRecipeInfo> findByIdsOrderByLastUsedAtDesc(List<Long> ids, int limit) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return query.selectFrom(savedRecipeInfo)
                .where(savedRecipeInfo.id.in(ids)
                        .and(savedRecipeInfo.lastUsedAt.isNotNull()))
                .orderBy(savedRecipeInfo.lastUsedAt.desc())
                .limit(limit)
                .fetch();
    }
}
