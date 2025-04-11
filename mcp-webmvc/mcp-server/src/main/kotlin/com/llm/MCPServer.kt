package com.llm
import com.llm.entity.Person
import com.llm.jpa.PersonRepository
import com.llm.tools.PersonTools
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.List

@SpringBootApplication
class MCPServer {

    @Bean
    fun loadData(personRepository: PersonRepository): CommandLineRunner {
        return CommandLineRunner { args: Array<String?>? ->
            val person1 = Person()
            person1.name = "John Doe"
            person1.nationality = "American"
            person1.age = 30

            val person2 = Person()
            person2.name = "Marie Durant"
            person2.nationality = "France"
            person2.age = 25

            val person3 = Person()
            person3.name = "Hans Schmidt"
            person3.nationality = "Germany"
            person3.age = 35

            personRepository.saveAll(List.of(person1, person2, person3))
            log.info("Data loaded: {} ", personRepository.findAll())
        }
    }

    @Bean
    fun tools(personTools: PersonTools): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
            .toolObjects(personTools)
            .build()
    }
}

val log: Logger = LoggerFactory
    .getLogger(MCPServer::class.java)


fun main(args: Array<String>) {
    runApplication<MCPServer>(*args)
}

