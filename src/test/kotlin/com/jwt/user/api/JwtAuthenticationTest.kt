package com.jwt.user.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class JwtAuthenticationTest : BaseIntegrationTest() {

    private val log = LoggerFactory.getLogger(JwtAuthenticationTest::class.java)

    @Test
    fun `JWT 토큰 없이 접근 시 401 확인 테스트`() {
        val getResult = mockMvc.perform(
                get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isUnauthorized)
            .andReturn()

        assertEquals(401, getResult.response.status)
    }
}
