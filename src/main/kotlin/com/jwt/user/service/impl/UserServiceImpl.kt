package com.jwt.user.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.jwt.comm.JwtEnums
import com.jwt.comm.JwtUtil
import com.jwt.comm.RedisComponent
import com.jwt.user.domain.entity.User
import com.jwt.user.domain.mapper.UserMapper
import com.jwt.user.domain.request.RequestUserCreateDto
import com.jwt.user.domain.request.RequestUserLoginDto
import com.jwt.user.domain.request.RequestUserSearchDto
import com.jwt.user.domain.request.RequestUserUpdateDto
import com.jwt.user.domain.response.ResponseJwtTokenDto
import com.jwt.user.domain.response.ResponsePageDto
import com.jwt.user.domain.response.ResponseUserDto
import com.jwt.user.domain.response.ResponseUserPageDto
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
    private val redisComponent: RedisComponent,
    private val objectMapper: ObjectMapper
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

        val accessToken = jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, userId)
        val refreshToken = jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, userId)

        redisComponent.setRefreshToken(userId+JwtEnums.TOKEN_KEY.value, refreshToken)  // refresh 토큰 Redis 저장

        return ResponseJwtTokenDto(
            accessToken = accessToken
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

    @Transactional
    override fun refreshToken(refreshToken: String): ResponseJwtTokenDto {
        val userId = SecurityContextHolder.getContext().authentication.name
        val userTokenKey = userId+JwtEnums.TOKEN_KEY.value
        val storedRefreshToken = requireNotNull(redisComponent.getRefreshToken(userTokenKey)){
            "로그인이 만료되었습니다. 다시 로그인하세요."
        }

        // refresh 토큰 비교
        if(storedRefreshToken != refreshToken) {
            redisComponent.setRefreshToken(userTokenKey, "") // 토큰 무효화
            throw SecurityException("비정상적인 접근입니다. 다시 로그인하세요.")
        }

        // refresh 토큰 검증
        if(!jwtUtil.validateToken(refreshToken, userId)) {
            throw IllegalStateException("유효하지 않은 리프레시 토큰입니다.")
        }

        // refresh 토큰 Redis 저장
        val newRefreshToken = jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, userId)
        redisComponent.setRefreshToken(userTokenKey, newRefreshToken)

        return ResponseJwtTokenDto(
            accessToken = jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, userId)
        )
    }

    override fun findPageUsers(userSearchDto: RequestUserSearchDto): ResponseUserPageDto {
        val page = userRepository.findPageUsers(userSearchDto)

        val responsePageDto = ResponsePageDto(
            page.totalPages,
            page.totalElements
        )

        val list = page.content.map { userMapper.toDto(it) }

        return ResponseUserPageDto(
            page = responsePageDto,
            list = list
        )
    }
}
