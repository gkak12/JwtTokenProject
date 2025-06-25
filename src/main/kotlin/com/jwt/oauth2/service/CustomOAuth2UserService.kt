package com.jwt.oauth2.service

import com.jwt.comm.AccountEnums
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService: DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val attributes = oAuth2User.attributes["response"] as? Map<*, *> ?: throw OAuth2AuthenticationException("Response not found")

        return DefaultOAuth2User(
            setOf(SimpleGrantedAuthority(AccountEnums.ROLE_USER.value)),
            attributes as Map<String, Any>,
            "email"
        )
    }
}