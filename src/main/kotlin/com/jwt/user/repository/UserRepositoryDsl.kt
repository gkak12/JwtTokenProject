package com.jwt.user.repository

import com.jwt.user.domain.entity.User
import com.jwt.user.domain.request.RequestUserSearchDto
import org.springframework.data.domain.Page

interface UserRepositoryDsl {

    fun findInfoByUsername(userId: String): User?
    fun findPageUsers(userSearchDto: RequestUserSearchDto): Page<User>
}
