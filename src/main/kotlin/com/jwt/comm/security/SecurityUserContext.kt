package com.jwt.comm.security

object SecurityUserContext {

    // ThreadLocal에 사용자 정보 저장
    private val userInfoThreadLocal: ThreadLocal<UserInfo> = ThreadLocal()

    // 현재 스레드에서 사용자 정보 조회
    val userInfo: UserInfo
        get() = userInfoThreadLocal.get()

    // 현재 스레드에 사용자 정보 등록
    fun setUserInfo(userInfo: UserInfo) {
        userInfoThreadLocal.set(userInfo)
    }

    // 현재 스레드에서 사용자 정보 삭제
    fun clear() {
        userInfoThreadLocal.remove()
    }

    // 사용자 아이디 조회
    val id: String
        get() = userInfo.id

    // 사용자 이름 조회
    val name: String
        get() = userInfo.name

    // 사용자 권한 조회
    val auth: String
        get() = userInfo.auth

    // 사용자 정보 내부 클래스
    data class UserInfo(
        val id: String,
        val name: String,
        val auth: String
    )
}