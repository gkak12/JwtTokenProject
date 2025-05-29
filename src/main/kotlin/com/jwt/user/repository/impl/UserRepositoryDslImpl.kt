package com.jwt.user.repository.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import com.jwt.user.repository.UserRepositoryDsl
import com.querydsl.jpa.impl.JPAQueryFactory
import com.jwt.user.domain.entity.QUser.user
import com.jwt.user.domain.entity.User

@Repository
class UserRepositoryDslImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : UserRepositoryDsl {

    private val log = LoggerFactory.getLogger(UserRepositoryDslImpl::class.java)

    override fun findInfoByUsername(userName: String): User? {
        return jpaQueryFactory
            .select(user)
            .from(user)
            .where(user.name.like("%"+userName+"%"))
            .fetchOne()
    }
}

