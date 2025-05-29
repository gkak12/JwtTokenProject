package com.jwt.comm

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil {

    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val validityAccessTime: Long = 1000 * 60 * 60 // 1시간
    private val validityRefreshTime: Long = 1000 * 60 * 60 * 24 * 14 // 2주

    // Access Token 생성
    fun createToken(flag:String, userId: String): String {
        val claims: Claims = Jwts.claims().setSubject(userId) // Token에 사용자 아이디 추가
        val now = Date()
        val validity = if (flag == "access") Date(now.time + validityAccessTime) else Date(now.time + validityRefreshTime)  // 만료 시간 설정

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
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
