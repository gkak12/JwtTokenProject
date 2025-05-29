package com.jwt.user.repository

import com.jwt.user.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>, UserRepositoryDsl {
}
