package com.jwt.user.domain.request

import jakarta.validation.constraints.NotBlank

data class RequestUserCreateDto(

    @field:NotBlank
    val id: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val auth: String
)
