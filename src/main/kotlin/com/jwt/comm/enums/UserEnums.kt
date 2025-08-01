package com.jwt.comm.enums

enum class UserEnums (val value: String, val msg: String) {
    REDIS_INFO(":REDIS_INFO", "REDIS 계정 키"),
    ROLE_USER("ROLE_USER", "일반 사용자 권한"),
    ROLE_ADMIN("ROLE_ADMIN", "일반 관리자 권한"),
    ROLE_ROOT_ADMIN("ROLE_ROOT_ADMIN", "루트 관리자 권한"),
    NAVER_OAUTH_EMAIL("email", "네이버 OAUTH 계정 이메일"),
    NAVER_OAUTH_NAME("name", "네이버 OAUTH 계정 이름")
}
