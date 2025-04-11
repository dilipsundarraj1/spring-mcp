package com.llm.tools

import com.llm.entity.Person
import com.llm.jpa.PersonRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service

@Service
class PersonTools(private val personRepository: PersonRepository) {

    @Tool(description = "Find person by ID")
    fun getPersonById(
        @ToolParam(description = "Person ID") id: Long
    ): Person? {
        log.info("getPersonById Person ID param : {} ", id)
        return personRepository.findById(id).orElse(null)
    }

    @Tool(description = "Find all persons by nationality")
    fun getPersonsByNationality(
        @ToolParam(description = "Nationality") nationality: String?
    ): List<Person>? {
        log.info("getPersonsByNationality Nationality param : {} ", nationality)
        val response = personRepository.findByNationality(nationality)
        log.info("Tool response : {} ", response)
        return response
    }

    companion object {
        private val log: Logger = LoggerFactory
            .getLogger(PersonTools::class.java)
    }
}