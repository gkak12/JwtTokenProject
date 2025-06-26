package com.jwt.comm.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthenticatedException(
    override val message: String = "인증이 필요합니다."
) : RuntimeException(message)