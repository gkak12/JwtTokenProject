package com.jwt.user.repository

import com.jwt.user.domain.entity.User

interface UserRepositoryDsl {

    fun findInfoByUsername(userId: String): User?
}
