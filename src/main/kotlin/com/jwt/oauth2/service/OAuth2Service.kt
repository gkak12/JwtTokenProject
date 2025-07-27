package com.jwt.oauth2.service

import com.jwt.comm.enums.UserEnums
import com.jwt.comm.enums.JwtEnums
import com.jwt.comm.util.JwtUtil
import com.jwt.user.domain.entity.User
import com.jwt.user.domain.mapper.UserMapper
import com.jwt.user.domain.response.ResponseJwtDto
import com.jwt.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class OAuth2Service (
    private val jwtUtil: JwtUtil,
    private val userMapper: UserMapper,
    private val userRepository: UserRepository
){
    fun createToken(encodedId: String): ResponseJwtDto{
        val id = String(Base64.getDecoder().decode(encodedId))
        val user = userRepository.findById(id).orElseThrow{
            NoSuchElementException("등록되지 않은 계정입니다.")
        }

        val userDto = userMapper.toDto(user)

        return ResponseJwtDto(
            jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, userDto),
            jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, userDto),
            "$id: OAuth2 로그인 성공했습니다."
        )
    }

    @Transactional
    fun createUserInfo(email: String, name: String){
        userRepository.findById(email).orElseGet{
            userRepository.save(
                User(email, "", name, UserEnums.ROLE_USER.value)
            )
        }
    }
}
