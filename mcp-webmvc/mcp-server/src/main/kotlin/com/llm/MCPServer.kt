package com.llm
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MCPServer

fun main(args: Array<String>) {
    runApplication<MCPServer>(*args)
}

