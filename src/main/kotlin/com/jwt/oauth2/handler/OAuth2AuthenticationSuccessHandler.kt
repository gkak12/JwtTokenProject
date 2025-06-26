package com.jwt.oauth2.handler

import com.jwt.oauth2.service.OAuth2Service
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.*

@Component
class OAuth2AuthenticationSuccessHandler(
    private val oAuth2Service: OAuth2Service
): AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val principal = authentication.principal as OAuth2User
        val email = principal.getAttribute<String>("email")
        val name = principal.getAttribute<String>("name")

        oAuth2Service.createUserInfo(email, name)

        val encodedEmail = Base64.getEncoder().encodeToString(email.toByteArray())
        response.sendRedirect("/oauth2/me?id=$encodedEmail")
    }
}