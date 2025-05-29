package com.jwt.user.domain.request

import jakarta.validation.constraints.NotBlank

data class RequestUserUpdateDto (
    @field:NotBlank
    val password: String,

    @field:NotBlank
    val name: String
)