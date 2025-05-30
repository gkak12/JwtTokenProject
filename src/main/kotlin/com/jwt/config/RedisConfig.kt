package com.jwt.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig (
    @Value("\${spring.data.redis.host}")
    private val host: String,

    @Value("\${spring.data.redis.port}")
    private val port: Int
){

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val configuration = RedisStandaloneConfiguration().apply {
            hostName = this@RedisConfig.host
            port = this@RedisConfig.port
        }

        return LettuceConnectionFactory(configuration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.setKeySerializer(StringRedisSerializer())
        redisTemplate.setValueSerializer(GenericJackson2JsonRedisSerializer())
        redisTemplate.setHashKeySerializer(StringRedisSerializer())
        redisTemplate.setHashValueSerializer(GenericJackson2JsonRedisSerializer())
        redisTemplate.setConnectionFactory(redisConnectionFactory())

        return redisTemplate
    }
}