package com.jwt.user.domain.response

data class ResponseUserPageDto (
    val list: List<ResponseUserDto>,
    val page: ResponsePageDto
)