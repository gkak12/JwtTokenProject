package com.jwt.oauth2.service

import com.jwt.comm.enums.AccountEnums
import com.jwt.comm.enums.JwtEnums
import com.jwt.comm.util.JwtUtil
import com.jwt.comm.util.RedisUtil
import com.jwt.user.domain.entity.User
import com.jwt.user.domain.response.ResponseLoginDto
import com.jwt.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class OAuth2Service (
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val redisUtil: RedisUtil
){
    fun createToken(encodedId: String): ResponseLoginDto{
        val id = String(Base64.getDecoder().decode(encodedId))
        val accessToken = jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, id)

        return ResponseLoginDto(accessToken)
    }

    @Transactional
    fun createUserInfo(email: String, name: String){
        val refreshToken = jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, email)
        redisUtil.setRefreshToken(email+ JwtEnums.TOKEN_KEY.value, refreshToken)

        userRepository.findById(email).orElseGet{
            userRepository.save(
                User(email, "", name, AccountEnums.ROLE_USER.value)
            )
        }
    }
}