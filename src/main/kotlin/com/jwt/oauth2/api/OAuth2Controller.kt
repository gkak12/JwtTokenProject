package com.jwt.oauth2.api

import com.jwt.oauth2.service.OAuth2Service
import com.jwt.user.domain.response.ResponseLoginDto
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth2")
class OAuth2Controller (
    private val oauth2Service: OAuth2Service
){

    @GetMapping("/me")
    fun findOAuth2Me(@RequestParam @Valid id: String, response: HttpServletResponse): ResponseEntity<ResponseLoginDto> {
        return ResponseEntity.ok(oauth2Service.createToken(id, response))
    }
}