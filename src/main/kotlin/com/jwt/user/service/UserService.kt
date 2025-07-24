package com.jwt.user.service

import com.jwt.user.domain.request.RequestUserCreateDto
import com.jwt.user.domain.request.RequestUserLoginDto
import com.jwt.user.domain.request.RequestUserSearchDto
import com.jwt.user.domain.request.RequestUserUpdateDto
import com.jwt.user.domain.response.ResponseJwtDto
import com.jwt.user.domain.response.ResponseUserDto
import com.jwt.user.domain.response.ResponseUserPageDto

interface UserService {
    fun createUser(userCreateDto: RequestUserCreateDto)
    fun loginUser(userLoginDto: RequestUserLoginDto): ResponseJwtDto
    fun findByUserId(userId: String): ResponseUserDto
    fun updateUser(userDto: RequestUserUpdateDto)
    fun deleteUser(userId: String)
    fun refreshToken(refreshToken: String): ResponseJwtDto
    fun findPageUsers(userSearchDto: RequestUserSearchDto): ResponseUserPageDto
}
