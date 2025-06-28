package com.jwt.user.repository

import com.jwt.user.domain.entity.User
import com.jwt.user.domain.request.RequestUserSearchDto
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(locations = ["classpath:application.yml"])
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryDslImplTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setupTestData(){
        val user1 = User(
            "test1",
            "password",
            "test1",
            "ROLE_USER",
        )
        val user2 = User(
            "test2",
            "password",
            "test2",
            "ROLE_USER",
        )
        val user3 = User(
            "test3",
            "password",
            "test3",
            "ROLE_USER",
        )
        val user4 = User(
            "test4",
            "password",
            "test4",
            "ROLE_USER",
        )
        val user5 = User(
            "test5",
            "password",
            "test5",
            "ROLE_USER",
        )
        val user6 = User(
            "test6",
            "password",
            "test6",
            "ROLE_USER",
        )

        userRepository.save(user1)
        userRepository.save(user2)
        userRepository.save(user3)
        userRepository.save(user4)
        userRepository.save(user5)
        userRepository.save(user6)
    }

    @Test
    fun `findPageUsers 테스트`(){
        // Given
        val userSearchDto = RequestUserSearchDto(
            pageNumber = 1,
            pageSize = 5,
            name = "test1",
            id = "test1"
        )

        // When
        val page = userRepository.findPageUsers(userSearchDto)

        // Then
        val expectElementCnt: Long = 1
        assertEquals(expectElementCnt, page.totalElements)
    }
}