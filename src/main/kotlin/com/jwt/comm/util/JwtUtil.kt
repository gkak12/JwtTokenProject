package com.jwt.comm.util

import com.jwt.comm.enums.JwtEnums
import com.jwt.user.domain.response.ResponseUserDto
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
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

    // Access/Refresh Token 생성
    fun createToken(type:String, userDto: ResponseUserDto): String {
        val claims: Claims = Jwts.claims().setSubject(userDto.id) // Token에 사용자 아이디 추가
        claims["NAME"] = userDto.name   // Token에 사용자 이름 추가
        claims["ROLE"] = userDto.auth   // Token에 사용자 권한 추가

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
            redisUtil.setRefreshToken(userDto.id+JwtEnums.TOKEN_KEY.value, token)
        }

        return token
    }

    // Token 유효성 검증
    fun validateToken(token: String, userId: String, userName: String, userAuth: String): Boolean {
        return !isTokenExpired(token) && userId == getUserId(token)
            && userName == getUserName(userName) && userAuth == getUserAuth(userAuth)
    }

    // Token에서 사용자 아이디 추출
    fun getUserId(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    // Token에서 사용자 이름 추출
    fun getUserName(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["NAME"] as String
    }

    // Token에서 사용자 권한 추출
    fun getUserAuth(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["ROLE"] as String
    }

    // Token 만료시간 검사
    fun isTokenExpired(token: String): Boolean {
        val expiration = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .expiration

        return expiration.before(Date())
    }
}
