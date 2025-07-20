package com.jwt.comm.util

import com.jwt.comm.enums.JwtEnums
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil(
    @Value("\${jwt.access-token-expiration}")
    private val validityAccessTime: Long,
    @Value("\${jwt.refresh-token-expiration}")
    private val validityRefreshTime: Long,
    private val redisUtil: RedisUtil
){

    private val log = LoggerFactory.getLogger(JwtUtil::class.java)
    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    // Access/Refresh Token 생성 및 쿠키 적용
    fun createToken(type:String, userId: String, response: HttpServletResponse) {
        val claims: Claims = Jwts.claims().setSubject(userId) // Token에 사용자 아이디 추가
        val now = Date()

        // 토큰 타입에 따른 만료 시간 설정
        val validityTime = if(type == JwtEnums.ACCESS_TYPE.value) validityAccessTime else validityRefreshTime
        val expirationTime = Date(now.time + validityTime)

        // 토큰 생성
        val token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expirationTime)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()

        val logMsg = if(type == JwtEnums.ACCESS_TYPE.value) "access_token: $token" else "refresh_token: $token"
        log.info(logMsg)

        if(type == JwtEnums.REFRESH_TYPE.value){    // refresh 토큰 Redis 저장
            redisUtil.setRefreshToken(userId+JwtEnums.TOKEN_KEY.value, token)
        }

        createCookie(type, token, validityTime, response)   // JWT 토큰 쿠키 저장 메서드 호출
    }

    // JWT 토큰을 쿠키에 저장
    private fun createCookie(type: String, token: String, validityTime: Long, response: HttpServletResponse) {
        val name = if(type == JwtEnums.ACCESS_TYPE.value) "access_token" else "refresh_token"

        val cookie = Cookie(name, token).apply {
            isHttpOnly = true
            secure = true
            maxAge = (validityTime / 1000).toInt()
            path = "/"
        }

        response.addCookie(cookie)
    }

    // Token에서 사용자 아이디 추출
    fun getUsername(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    // Token 유효성 검사
    fun isTokenExpired(token: String): Boolean {
        val expiration = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .expiration

        return expiration.before(Date())
    }

    // Token 검증
    fun validateToken(token: String, userId: String): Boolean {
        return userId == getUsername(token) && !isTokenExpired(token)
    }
}
