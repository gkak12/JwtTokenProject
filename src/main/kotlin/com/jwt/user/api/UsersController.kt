package com.jwt.user.api

import com.jwt.comm.JwtEnums
import com.jwt.comm.JwtUtil
import com.jwt.user.domain.request.RequestUserCreateDto
import com.jwt.user.domain.request.RequestUserLoginDto
import com.jwt.user.domain.request.RequestUserSearchDto
import com.jwt.user.domain.request.RequestUserUpdateDto
import com.jwt.user.domain.response.ResponseJwtTokenDto
import com.jwt.user.domain.response.ResponseUserDto
import com.jwt.user.domain.response.ResponseUserPageDto
import com.jwt.user.service.UserService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UsersController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/signup")
    fun signup(@RequestBody @Valid userCreateDto: RequestUserCreateDto): ResponseEntity<Void> {
        userService.createUser(userCreateDto)
        return ResponseEntity.ok().build()
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    fun login(@RequestBody @Valid userLoginDto: RequestUserLoginDto): ResponseEntity<ResponseJwtTokenDto> {
        return ResponseEntity.ok(userService.loginUser(userLoginDto))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    fun find(): ResponseEntity<ResponseUserDto> {
        val userId = SecurityContextHolder.getContext().authentication.name
        return ResponseEntity.ok(userService.findByUserId(userId))
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/me")
    fun update(@RequestBody @Valid userUpdateDto: RequestUserUpdateDto): ResponseEntity<Void> {
        userService.updateUser(userUpdateDto)
        return ResponseEntity.ok().build()
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/me")
    fun delete(): ResponseEntity<Void> {
        val userId = SecurityContextHolder.getContext().authentication.name
        userService.deleteUser(userId)
        return ResponseEntity.ok().build()
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refreshToken")
    fun refreshToken(@RequestHeader("Authorization") token: String): ResponseEntity<ResponseJwtTokenDto> {
        val refreshToken = token.removePrefix(JwtEnums.BEARER.value)
        val tokens = userService.refreshToken(refreshToken)
        return ResponseEntity.ok(tokens)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/page")
    fun findPageUsers(@ParameterObject userSearchDto: RequestUserSearchDto): ResponseEntity<ResponseUserPageDto>{
        return ResponseEntity.ok(userService.findPageUsers(userSearchDto))
    }
}