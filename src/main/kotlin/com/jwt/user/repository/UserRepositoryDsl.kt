package com.jwt.user.repository

import com.jwt.user.domain.entity.User

interface UserRepositoryDsl {

    fun findByUserName(userName: String): User?
}
