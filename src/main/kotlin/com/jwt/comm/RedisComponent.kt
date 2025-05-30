package com.jwt.comm

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component("redisComponent")
class RedisComponent (
    private val redisTemplate: RedisTemplate<String, String>
){
    fun setToken(key:String, token: String){
        redisTemplate.opsForValue().set(key, token, 1209600, TimeUnit.SECONDS)
    }

    fun getToken(key:String): String?{
        return redisTemplate.opsForValue().get(key)
    }
}