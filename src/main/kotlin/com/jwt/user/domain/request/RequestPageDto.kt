package com.jwt.user.domain.request

open class RequestPageDto(
    var pageNumber: Int = 1,
    var pageSize: Int = 10
){
    val pageNum: Int
        get() = pageNumber-1
}