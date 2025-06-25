package com.jwt.oauth2.service

import com.jwt.comm.JwtEnums
import com.jwt.comm.JwtUtil
import com.jwt.user.domain.response.ResponseJwtTokenDto
import org.springframework.stereotype.Service

@Service
class OAuth2Service (
    private val jwtUtil: JwtUtil
){
    fun createToken(id: String): ResponseJwtTokenDto{
        val accessToken = jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, id)
        return ResponseJwtTokenDto(accessToken)
    }
}