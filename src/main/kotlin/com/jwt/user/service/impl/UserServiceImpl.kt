package com.jwt.user.service.impl

import com.jwt.comm.enums.JwtEnums
import com.jwt.comm.security.SecurityUserContext
import com.jwt.comm.util.JwtUtil
import com.jwt.comm.util.RedisUtil
import com.jwt.user.domain.entity.User
import com.jwt.user.domain.mapper.UserMapper
import com.jwt.user.domain.request.RequestUserCreateDto
import com.jwt.user.domain.request.RequestUserLoginDto
import com.jwt.user.domain.request.RequestUserSearchDto
import com.jwt.user.domain.request.RequestUserUpdateDto
import com.jwt.user.domain.response.ResponseJwtDto
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
    private val redisUtil: RedisUtil
) : UserService {

    private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)

    @Transactional
    override fun createUser(userCreateDto: RequestUserCreateDto) {
        if (userRepository.findById(userCreateDto.id).isPresent) {
            val msg = "${userCreateDto.id}: 아이디가 중복됩니다."

            log.error(msg)
            throw IllegalStateException(msg)
        }

        val encodedPassword = bCryptPasswordEncoder.encode(userCreateDto.password)

        val user: User = userMapper.toCreateEntity(userCreateDto).apply {
            password = encodedPassword
        }
        userRepository.save(user)
    }

    @Transactional
    override fun loginUser(userLoginDto: RequestUserLoginDto): ResponseJwtDto {
        val userId = userLoginDto.id
        val userPassword = userLoginDto.password

        val user: User = userRepository.findById(userId).orElseThrow {
            val msg = "$userId: 로그인 계정이 존재하지 않습니다."

            log.error(msg)
            NoSuchElementException(msg)
        }

        val userDto = userMapper.toDto(user)

        if (!bCryptPasswordEncoder.matches(userPassword, user.password)) {
            val msg = "$userId: 로그인 시도한 계정 비밀번호가 일치하지 않습니다."
            
            log.error(msg)
            throw BadCredentialsException(msg)
        }

        return ResponseJwtDto(
            accessToken = jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, userDto),
            refreshToken = jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, userDto),
            msg = "$userId: 로그인 성공"
        )
    }

    override fun findByUserId(userId: String): ResponseUserDto {
        val user = userRepository.findById(userId).orElseThrow {
            val msg = "$userId: 조회 대상 계정이 존재하지 않습니다."

            log.error(msg)
            NoSuchElementException(msg)
        }

        return userMapper.toDto(user)
    }

    @Transactional
    override fun updateUser(userDto: RequestUserUpdateDto) {
        val userId = SecurityContextHolder.getContext().authentication.name
        val user = userRepository.findById(userId).orElseThrow {
            val msg = "$userId: 수정 대상 계정이 존재하지 않습니다."

            log.error(msg)
            NoSuchElementException(msg)
        }

        user.password = bCryptPasswordEncoder.encode(userDto.password)
        user.name = userDto.name

        userRepository.save(user)
    }

    @Transactional
    override fun deleteUser(userId: String) {
        val user = userRepository.findById(userId).orElseThrow {
            val msg = "$userId: 삭제 대상 계정이 존재하지 않습니다."

            log.error(msg)
            NoSuchElementException(msg)
        }

        userRepository.delete(user)
    }

    @Transactional
    override fun refreshToken(refreshToken: String): ResponseJwtDto {
        val userId = SecurityUserContext.id
        val userName = SecurityUserContext.name
        val userAuth = SecurityUserContext.auth
        val userDto = ResponseUserDto(userId, SecurityUserContext.name, SecurityUserContext.auth)
        val userTokenKey = userId+JwtEnums.TOKEN_KEY.value
        val storedRefreshToken = requireNotNull(redisUtil.getRefreshToken(userTokenKey)){
            "로그인이 만료되었습니다. 다시 로그인하세요."
        }

        // refresh 토큰 비교
        if(storedRefreshToken != refreshToken) {
            redisUtil.setRefreshToken(userTokenKey, "") // 토큰 무효화
            throw SecurityException("비정상적인 접근입니다. 다시 로그인하세요.")
        }

        // refresh 토큰 검증
        if(!jwtUtil.validateToken(refreshToken, userId, userName, userAuth)) {
            throw IllegalStateException("유효하지 않은 리프레시 토큰입니다.")
        }

        return ResponseJwtDto(
            accessToken = jwtUtil.createToken(JwtEnums.ACCESS_TYPE.value, userDto),
            refreshToken = jwtUtil.createToken(JwtEnums.REFRESH_TYPE.value, userDto),
            msg = "JWT 토큰 재발급 되었습니다."
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
