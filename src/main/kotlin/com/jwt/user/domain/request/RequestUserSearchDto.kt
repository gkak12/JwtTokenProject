package com.jwt.user.domain.request

data class RequestUserSearchDto(
    val id: String? = null,
    val name: String? = null,
) : RequestPageDto()