package com.jwt.comm.enums

enum class JwtEnums (val value: String, val msg: String) {
    BEARER("Bearer ", "HTTP 헤더 JWT 키"),
    TOKEN_KEY(":token", "REDIS 사용자 REFRESH 키"),
    REFRESH_TYPE("refresh", "JWT REFRESH 토큰 타입"),
    ACCESS_TYPE("access", "JWT ACCESS 토큰 타입"),
    CLAIMS_NAME("NAME", "JWT 클레임 사용자 이름"),
    CLAIMS_ROLE("ROLE", "JWT 클레임 사용자 권한")
}
