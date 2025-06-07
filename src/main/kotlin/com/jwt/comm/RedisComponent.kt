package com.jwt.comm

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component("redisComponent")
class RedisComponent (
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${jwt.refresh-token-expiration}")
    private val validityRefreshTime: Long
){
    fun setRefreshToken(key:String, token: String){
        redisTemplate.opsForValue().set(key, token, validityRefreshTime, TimeUnit.MILLISECONDS)
    }

    fun getRefreshToken(key:String): String?{
        return redisTemplate.opsForValue().get(key)
    }
}