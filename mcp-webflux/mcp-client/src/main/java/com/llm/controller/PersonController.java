package com.llm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final static Logger log = LoggerFactory
        .getLogger(PersonController.class);
    private final ChatClient chatClient;

    public PersonController(ChatClient.Builder chatClientBuilder,
                            ToolCallbackProvider tools) {
        this.chatClient = chatClientBuilder
                .defaultTools(tools)
                .build();
    }

    @GetMapping("/nationality/{nationality}")
    Flux<ChatResponse> findByNationality(@PathVariable String nationality) {

        log.info("nationality : {} ", nationality);

        PromptTemplate pt = new PromptTemplate("""
                Find persons with {nationality} nationality.
                """);
        Prompt p = pt.create(Map.of("nationality", nationality));
        return this.chatClient.prompt(p)
                .stream()
                .chatResponse()
                .subscribeOn(Schedulers.boundedElastic());
    }


    @GetMapping("/count-by-nationality/{nationality}")
    Flux<ChatResponse> countByNationality(@PathVariable String nationality) {
        PromptTemplate pt = new PromptTemplate("""
                How many persons come from {nationality} ?
                """);
        Prompt p = pt.create(Map.of("nationality", nationality));
        return this.chatClient.prompt(p)
                .stream()
                .chatResponse()
                .subscribeOn(Schedulers.boundedElastic());
    }
}