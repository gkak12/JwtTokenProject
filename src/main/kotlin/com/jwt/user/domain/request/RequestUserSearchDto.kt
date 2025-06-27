package com.jwt.user.domain.request

class RequestUserSearchDto(
    var id: String? = null,
    var name: String? = null,
    pageNumber: Int? = null,
    pageSize: Int? = null
) : RequestPageDto(pageNumber, pageSize)