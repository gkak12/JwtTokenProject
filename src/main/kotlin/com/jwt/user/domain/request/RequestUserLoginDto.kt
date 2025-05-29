package com.jwt.user.domain.request

import jakarta.validation.constraints.NotBlank

data class RequestUserLoginDto(

    @field:NotBlank
    val id: String,

    @field:NotBlank
    val password: String
)
