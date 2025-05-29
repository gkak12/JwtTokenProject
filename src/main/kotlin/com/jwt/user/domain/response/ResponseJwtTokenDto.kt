package com.jwt.user.domain.response

data class ResponseJwtTokenDto (
    val accessToken: String,
    val refreshToken: String
)