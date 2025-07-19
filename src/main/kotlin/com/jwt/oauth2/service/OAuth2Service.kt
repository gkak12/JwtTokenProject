package com.jwt.oauth2.service

import com.jwt.comm.enums.AccountEnums
import com.jwt.comm.enums.JwtEnums
import com.jwt.comm.util.JwtUtil
import com.jwt.comm.util.RedisUtil
import com.jwt.user.domain.entity.User
import com.jwt.user.domain.response.ResponseLoginDto
import com.jwt.user.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class OAuth2Service (
    private val jwtUtil: JwtUtil,
    private val redisUtil: RedisUtil,
    private val userRepository: UserRepository
){
    fun createToken(encodedId: String, response: HttpServletResponse): ResponseLoginDto{
        val id = String(Base64.getDecoder().decode(encodedId))
        val userTokenKey = id+JwtEnums.TOKEN_KEY.value

        requireNotNull(redisUtil.getRefreshToken(userTokenKey)){
            "등록되지 않은 계정입니다."
        }

        return ResponseLoginDto("$id: OAuth2 로그인 성공했습니다.")
    }

    @Transactional
    fun createUserInfo(email: String, name: String, response: HttpServletResponse){
        jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, email, response)
        jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, email, response)

        userRepository.findById(email).orElseGet{
            userRepository.save(
                User(email, "", name, AccountEnums.ROLE_USER.value)
            )
        }
    }
}