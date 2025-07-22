package com.jwt.user.domain.response

data class ResponseJwtDto (
    val accessToken: String,
    val refreshToken: String,
    val msg: String
)