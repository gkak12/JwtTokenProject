package com.jwt.user.service.impl

import com.jwt.comm.JwtUtil
import com.jwt.user.domain.entity.User
import com.jwt.user.domain.mapper.UserMapper
import com.jwt.user.domain.request.RequestUserCreateDto
import com.jwt.user.domain.request.RequestUserLoginDto
import com.jwt.user.domain.request.RequestUserUpdateDto
import com.jwt.user.domain.response.ResponseJwtTokenDto
import com.jwt.user.domain.response.ResponseUserDto
import com.jwt.user.repository.UserRepository
import com.jwt.user.service.UserService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val jwtUtil: JwtUtil,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
) : UserService {

    private val log = LoggerFactory.getLogger(com.jwt.user.service.impl.UserServiceImpl::class.java)

    @Transactional
    override fun createUser(userCreateDto: RequestUserCreateDto) {
        if (userRepository.findById(userCreateDto.id).isPresent) {
            throw IllegalStateException("아이디가 중복됩니다.")
        }

        val encodedPassword = bCryptPasswordEncoder.encode(userCreateDto.password)

        val user: User = userMapper.toCreateEntity(userCreateDto).apply {
            password = encodedPassword
        }
        userRepository.save(user)
    }

    @Transactional
    override fun loginUser(userLoginDto: RequestUserLoginDto): ResponseJwtTokenDto {
        val userId = userLoginDto.id
        val userPassword = userLoginDto.password

        val user: User = userRepository.findById(userId).orElseThrow {
            NoSuchElementException("로그인 계정이 존재하지 않습니다.")
        }

        if (!bCryptPasswordEncoder.matches(userPassword, user.password)) {
            throw BadCredentialsException("입력한 비밀번호가 일치하지 않습니다.")
        }

        var accessToken = jwtUtil.createToken("access", userId)
        var refreshToken = jwtUtil.createToken("refresh", userId)

        // refresh 토큰 저장
        user.token = refreshToken
        userRepository.save(user)

        return ResponseJwtTokenDto(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    override fun findByUserId(userId: String): ResponseUserDto {
        val user = userRepository.findById(userId).orElseThrow {
            NoSuchElementException("조회 대상 계정이 존재하지 않습니다.")
        }
        return userMapper.toDto(user)
    }

    @Transactional
    override fun updateUser(userDto: RequestUserUpdateDto) {
        val userId = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findById(userId).orElseThrow {
            NoSuchElementException("수정 대상 계정이 존재하지 않습니다.")
        }

        user.password = bCryptPasswordEncoder.encode(userDto.password)
        user.name = userDto.name

        userRepository.save(user)
    }

    @Transactional
    override fun deleteUser(userId: String) {
        val user = userRepository.findById(userId).orElseThrow {
            NoSuchElementException("삭제 대상 계정이 존재하지 않습니다.")
        }

        userRepository.delete(user)
    }
}
