package com.jwt.user.domain.mapper

import com.jwt.user.domain.entity.User
import com.jwt.user.domain.request.RequestUserCreateDto
import com.jwt.user.domain.response.ResponseUserDto
import org.mapstruct.*

@Mapper(
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = "spring"
)
interface UserMapper {

    fun toCreateEntity(userCreateDto: RequestUserCreateDto): User
    fun toDto(user: User): ResponseUserDto
}
