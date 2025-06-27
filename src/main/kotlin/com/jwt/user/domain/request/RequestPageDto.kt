package com.jwt.user.domain.request

open class RequestPageDto(
    var pageNumber: Int? = null,
    var pageSize: Int? = null
){
    fun getPageNumber(): Int{
        return (pageNumber ?: 1) - 1
    }
}