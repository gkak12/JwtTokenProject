package com.jwt.user.domain.request

open class RequestPageDto(
    val pageNumber: Int? = null,
    val pageSize: Int? = null,
    val pageOffset: Int? = null
){
    fun getPageNumber(): Int{
        return (pageNumber ?: 1) - 1
    }
}