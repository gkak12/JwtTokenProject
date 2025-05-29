package com.jwt

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.jwt"])
class MainApplication

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger(MainApplication::class.java)
    log.info("Starting MainApplication...")
    runApplication<MainApplication>(*args)
}
