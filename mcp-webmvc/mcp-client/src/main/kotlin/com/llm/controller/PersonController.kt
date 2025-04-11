package com.llm.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.util.Map

@RestController
@RequestMapping("/persons")
class PersonController(
    chatClientBuilder: ChatClient.Builder,
    tools: ToolCallbackProvider
) {
    private val chatClient: ChatClient = chatClientBuilder
        .defaultTools(tools)
        .build()

    @GetMapping("/nationality/{nationality}")
    fun findByNationality(@PathVariable nationality: String): String? {
        log.info("nationality : {} ", nationality)

        val pt = PromptTemplate(
            """
                Find persons with {nationality} nationality.
                
                """.trimIndent()
        )
        val p = pt.create(Map.of<String, Any>("nationality", nationality))
        val response =  chatClient.prompt(p)
            .call();

        log.info("response: $response")
        return response.content()
    }


    @GetMapping("/count-by-nationality/{nationality}")
    fun countByNationality(@PathVariable nationality: String): Flux<ChatResponse> {
        val pt = PromptTemplate(
            """
                How many persons come from {nationality} ?
                
                """.trimIndent()
        )
        val p = pt.create(Map.of<String, Any>("nationality", nationality))
        return chatClient.prompt(p)
            .stream()
            .chatResponse()
            .subscribeOn(Schedulers.boundedElastic())
    }

    companion object {
        private val log: Logger = LoggerFactory
            .getLogger(PersonController::class.java)
    }
}