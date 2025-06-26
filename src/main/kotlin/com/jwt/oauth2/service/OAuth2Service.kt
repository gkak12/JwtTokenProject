package com.jwt.oauth2.service

import com.jwt.comm.JwtEnums
import com.jwt.comm.JwtUtil
import com.jwt.user.domain.response.ResponseJwtTokenDto
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class OAuth2Service (
    private val jwtUtil: JwtUtil
){
    fun createToken(encodedId: String): ResponseJwtTokenDto{
        val id = String(Base64.getDecoder().decode(encodedId))
        val accessToken = jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, id)
        return ResponseJwtTokenDto(accessToken)
    }
}