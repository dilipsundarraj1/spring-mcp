package com.llm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactiveMCPClient {
}

fun main(args: Array<String>) {
    runApplication<ReactiveMCPClient>(*args)
}

