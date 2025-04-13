package com.llm.controller

import com.llm.dtos.UserInput
import io.modelcontextprotocol.client.McpSyncClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class ChatsController(
    private val chatClientBuilder: ChatClient.Builder,
    private val mcpClients: List<McpSyncClient>
) {

    private val chatClient: ChatClient = chatClientBuilder
        .defaultTools(SyncMcpToolCallbackProvider(mcpClients))
        .build()


    @PostMapping("/v1/chats")
    fun chat(@RequestBody @Valid userInput: UserInput): String? {
        log.info("userInput message : {} ", userInput)
        val requestSpec: ChatClient.ChatClientRequestSpec =
            chatClient.prompt()
                .user(userInput.prompt)

        log.info("requestSpec : {} ", requestSpec)

        val responseSpec = requestSpec.call()
        log.info("responseSpec1 : {} ", responseSpec)
        log.info("content : {} ", responseSpec.content())
        return responseSpec.content()
    }


    companion object {
        private val log: Logger = LoggerFactory
            .getLogger(ChatsController::class.java)
    }
}