package com.jwt.oauth2.service

import com.jwt.comm.enums.AccountEnums
import com.jwt.comm.enums.JwtEnums
import com.jwt.comm.util.JwtUtil
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
    private val userRepository: UserRepository
){
    fun createToken(encodedId: String, response: HttpServletResponse): ResponseLoginDto{
        val id = String(Base64.getDecoder().decode(encodedId))
        jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, id, response)
        jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, id, response)

        return ResponseLoginDto("$id: JWT 토큰 발급 되었습니다.")
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