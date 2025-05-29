package com.jwt.comm

import com.jwt.user.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userService: UserService
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = request.requestURI

        if (uri.startsWith("/users/login") || uri.startsWith("/users/signup")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = getTokenFromRequest(request)
            val userId = jwtUtil.getUsername(token ?: throw IllegalArgumentException("Token is missing"))

            userService.findByUserId(userId) // 삭제된 계정인지 확인

            if (jwtUtil.validateToken(token, userId)) {
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val authentication = UsernamePasswordAuthenticationToken(userId, null, authorities)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            log.info("JWT Filter Exception: ${e.message}")
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization")
        return if (header != null && header.startsWith("Bearer ")) {
            header.substring(7)
        } else null
    }
}
