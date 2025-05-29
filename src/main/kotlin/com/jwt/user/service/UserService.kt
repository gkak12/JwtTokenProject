package com.jwt.user.service

import com.jwt.user.domain.request.RequestUserCreateDto
import com.jwt.user.domain.request.RequestUserLoginDto
import com.jwt.user.domain.request.RequestUserUpdateDto
import com.jwt.user.domain.response.ResponseJwtTokenDto
import com.jwt.user.domain.response.ResponseUserDto

interface UserService {
    fun createUser(userCreateDto: RequestUserCreateDto)
    fun loginUser(userLoginDto: RequestUserLoginDto): ResponseJwtTokenDto
    fun findByUserId(userId: String): ResponseUserDto
    fun updateUser(userDto: RequestUserUpdateDto)
    fun deleteUser(userId: String)
}