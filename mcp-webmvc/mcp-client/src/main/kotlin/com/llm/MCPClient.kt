package com.llm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MCPClient {
}

fun main(args: Array<String>) {
    runApplication<MCPClient>(*args)
}

