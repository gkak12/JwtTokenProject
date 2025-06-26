package com.jwt.oauth2.service

import com.jwt.comm.AccountEnums
import com.jwt.comm.JwtEnums
import com.jwt.comm.JwtUtil
import com.jwt.comm.RedisComponent
import com.jwt.user.domain.entity.User
import com.jwt.user.domain.response.ResponseJwtTokenDto
import com.jwt.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class OAuth2Service (
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val redisComponent: RedisComponent
){
    fun createToken(encodedId: String): ResponseJwtTokenDto{
        val id = String(Base64.getDecoder().decode(encodedId))
        val accessToken = jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, id)

        return ResponseJwtTokenDto(accessToken)
    }

    @Transactional
    fun createUserInfo(email: String, name: String){
        val refreshToken = jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, email)
        redisComponent.setRefreshToken(email+JwtEnums.TOKEN_KEY.value, refreshToken)

        userRepository.findById(email).orElseGet{
            userRepository.save(
                User(email, "", name, AccountEnums.ROLE_USER.value)
            )
        }
    }
}