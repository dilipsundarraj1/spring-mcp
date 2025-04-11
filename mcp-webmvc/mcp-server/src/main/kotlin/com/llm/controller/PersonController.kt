package com.llm.controller

import com.llm.entity.Person
import com.llm.jpa.PersonRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PersonController(private val personRepository: PersonRepository) {
    companion object {
        private val log: Logger = LoggerFactory
            .getLogger(PersonController::class.java)
    }


    @GetMapping("/person/{id}")
    fun getPersonById(@PathVariable @ToolParam(description = "Person ID") id: Long): Person? {
        log.info("getPersonById Person ID param : {} ", id)
        return personRepository.findById(id).orElse(null)
    }


    /**
     * This tool call
     */
//    @GetMapping("/person/nationality/{id}")
//    @Tool(description = "Find all persons by nationality")
//    fun getPersonsByNationality(
//        @ToolParam(description = "Nationality") nationality: String?
//    ): List<Person>? {
//        log.info("getPersonsByNationality Nationality param : {} ", nationality)
//        val response = personRepository.findByNationality(nationality)
//        log.info("Tool response : {} ", response)
//        return response
//    }
}