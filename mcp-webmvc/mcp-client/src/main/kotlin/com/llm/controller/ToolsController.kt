package com.llm.controller

import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.spec.McpSchema
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ToolsController(private val mcpClients: List<McpSyncClient>) {
    companion object {
        private val log: Logger = LoggerFactory
            .getLogger(ChatsController::class.java)
    }

    @GetMapping("/v1/tools")
    fun tools(): List<McpSchema.ListToolsResult> {
        mcpClients.forEach { client ->
            log.info("serverInfo : {} , tools : {} ", client.serverInfo, client.listTools())
        }

        return mcpClients.map { it.listTools() }
    }
}