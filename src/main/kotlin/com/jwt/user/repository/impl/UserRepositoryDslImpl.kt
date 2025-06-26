package com.jwt.user.repository.impl

import com.jwt.user.domain.entity.QUser.user
import com.jwt.user.domain.entity.User
import com.jwt.user.domain.request.RequestUserSearchDto
import com.jwt.user.repository.UserRepositoryDsl
import com.querydsl.jpa.impl.JPAQueryFactory
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

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

    override fun findPageUsers(userSearchDto: RequestUserSearchDto): Page<User> {
        val pageNumber = userSearchDto.getPageNumber()
        val pageSize = userSearchDto.pageSize ?: 10

        val pageable = PageRequest.of(
            pageNumber,
            pageSize
        )

        val list = jpaQueryFactory
            .select(user)
            .from(user)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val count = jpaQueryFactory
            .select(user.count())
            .from(user)
            .fetchOne() ?: 0L

        return PageImpl(list, pageable, count)
    }
}

