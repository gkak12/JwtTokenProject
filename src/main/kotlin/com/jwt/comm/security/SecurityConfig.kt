package com.jwt.comm.security

import com.jwt.comm.util.JwtUtil
import com.jwt.comm.util.RedisUtil
import com.jwt.oauth2.service.CustomOAuth2UserService
import com.jwt.oauth2.handler.OAuth2AuthenticationFailureHandler
import com.jwt.oauth2.handler.OAuth2AuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val successHandler: OAuth2AuthenticationSuccessHandler,
    private val failureHandler: OAuth2AuthenticationFailureHandler
) {

    @Bean
    fun jwtAuthenticationFilter(jwtUtil: JwtUtil, redisUtil: RedisUtil): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(jwtUtil, redisUtil)
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests {
                it.requestMatchers(*SecurityAuthWhiteList.paths).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login {
                it.userInfoEndpoint{endpoint -> endpoint.userService(customOAuth2UserService)}
                    .successHandler(successHandler)
                    .failureHandler(failureHandler)
            }

        return http.build()
    }
}
