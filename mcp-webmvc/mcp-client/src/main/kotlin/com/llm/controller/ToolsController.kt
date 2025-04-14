package com.llm.controller

import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.spec.McpSchema
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.mcp.SyncMcpToolCallback
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
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

    @GetMapping("/v1/tools/invoke")
    fun invokeTools(
        @RequestParam("tool_name") toolName: String,
        @RequestParam("tool_input_key") toolInputKey: String,
        @RequestParam("tool_input_value") toolInputValue: String,
        @RequestParam("mcp_server") mcpServer: String,

    ): Any {

        val mcpClient = mcpClients.firstOrNull { it.clientInfo.name.contains(mcpServer, true) }

        if (mcpClient == null)
            return ResponseEntity.badRequest().body("MCP server not found: $mcpServer")

        log.info("clientInfo : {} , serverCapabilities : {}  ", mcpClient.clientInfo, mcpClient.serverCapabilities)

        val mcpTool = mcpClient.listTools()
            .tools.first { it.name == toolName }

        log.info("mcpTool : {} ", mcpTool)
        return  if(mcpTool == null) {
            log.error("Tool not found: $toolName")
            "Tool not found: $toolName"
        }else{
            val callback: ToolCallback = SyncMcpToolCallback(mcpClient, mcpTool)
            val inputJson =
                """
                {
                    "$toolInputKey": "$toolInputValue"
                }""".trimIndent()
            log.info("inputJson : {} ", inputJson)
             val toolCallResult = callback.call(inputJson)
            log.info("Tool call result: $toolCallResult")
            toolCallResult
        }
    }
}