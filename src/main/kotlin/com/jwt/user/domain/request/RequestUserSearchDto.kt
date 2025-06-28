package com.jwt.user.domain.request

class RequestUserSearchDto(
    pageNumber: Int,
    pageSize: Int,
    var id: String = "",
    var name: String = ""
) : RequestPageDto(pageNumber, pageSize)