package com.jwt.oauth2.handler

import com.jwt.comm.AccountEnums
import com.jwt.comm.JwtEnums
import com.jwt.comm.JwtUtil
import com.jwt.comm.RedisComponent
import com.jwt.user.domain.entity.User
import com.jwt.user.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.Base64

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val redisComponent: RedisComponent
): AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val principal = authentication.principal as OAuth2User
        val email = principal.getAttribute<String>("email")
        val name = principal.getAttribute<String>("name")

        val refreshToken = jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, email)

        findOrCreateUser(email, name)
        redisComponent.setRefreshToken(email+JwtEnums.TOKEN_KEY.value, refreshToken)

        var encodedEmail = Base64.getEncoder().encodeToString(email.toByteArray())
        response.sendRedirect("/oauth2/me?id=$encodedEmail")
    }

    @Transactional
    fun findOrCreateUser(email: String, name: String) {
        userRepository.findById(email) ?: userRepository.save(
            User(email, "", name, AccountEnums.ROLE_USER.value)
        )
    }
}