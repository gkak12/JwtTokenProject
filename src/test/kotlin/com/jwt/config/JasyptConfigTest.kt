package com.jwt.config

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.Base64

class JasyptConfigTest {

    private val log = LoggerFactory.getLogger(JasyptConfigTest::class.java)

    private fun jasyptEncoding(value: String): String {
        val pbeEnc = StandardPBEStringEncryptor()
        pbeEnc.setAlgorithm("PBEWithMD5AndDES")
        pbeEnc.setPassword(System.getenv("JASYPT_KEY"))

        return pbeEnc.encrypt(value)
    }

    private fun jasyptDecoding(value: String): String {
        val pbeEnc = StandardPBEStringEncryptor()
        pbeEnc.setAlgorithm("PBEWithMD5AndDES")
        pbeEnc.setPassword(System.getenv("JASYPT_KEY"))

        return pbeEnc.decrypt(
                    value
                        .replace("ENC(", "")
                        .replace(")", "")
                )
    }

    @Test
    fun `jasypt 인코딩 테스트`(){
        val username = "admin"
        val password = "admin"

        log.info("username: {}", jasyptEncoding(username))
        log.info("password: {}", jasyptEncoding(password))
    }

    @Test
    fun `jasypt 디코딩 테스트`(){
        val username = "ENC(0quG6FVbayjtAMT/26YQxA==)"
        val password = "ENC(cOh6l6GKbZQdo8p9EE98/A==)"

        log.info("username: {}", jasyptDecoding(username))
        log.info("password: {}", jasyptDecoding(password))
    }

    @Test
    fun `Base64 인코딩 테스트`(){
        val email = "test_user@email.com"
        val encodedEmail = Base64.getEncoder().encodeToString(email.toByteArray())

        log.info("encodedEmail: {}", encodedEmail)
    }

    @Test
    fun `Base64 디코딩 테스트`(){
        val encodedEmail = "dGVzdF91c2VyQGVtYWlsLmNvbQ=="
        val decodedEmail = String(Base64.getDecoder().decode(encodedEmail))

        log.info("decodedEmail: {}", decodedEmail)
    }
}