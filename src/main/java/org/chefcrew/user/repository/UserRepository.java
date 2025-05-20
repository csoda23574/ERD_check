package org.chefcrew.user.repository;

import static org.chefcrew.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.chefcrew.user.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public UserRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }
    @Transactional
    public void addUser(User user) {
        em.persist(user);
    }

    public User findById(long userId) {
        return query.selectFrom(user)
                .where(user.userId.eq(userId))
                .fetchFirst();
    }

    public boolean existByEmail(String email) {
        Long count = query.select(user.count())
                .from(user)
                .where(user.email.eq(email))
                .fetchOne();
        return count != null && count > 0;
    }

    public User findByEmail(String email) {
        return query.selectFrom(user)
                .where(user.email.eq(email))
                .fetchFirst();
    }

    public User findByKakaoId(long kakaoId) {
        return query.selectFrom(user)
                .where(user.kakaoId.eq(kakaoId))
                .fetchFirst();
    }
}
