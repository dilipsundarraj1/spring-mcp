package com.llm.config

import com.llm.person.entity.Person
import com.llm.person.jpa.PersonRepository
import com.llm.person.PersonTools
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersonDataLoader {
    companion object {
        private val log: Logger = LoggerFactory
            .getLogger(PersonTools::class.java)
    }
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

            personRepository.saveAll(listOf(person1, person2, person3))
            log.info("Person Data loaded: {} ", personRepository.findAll())
        }
    }
}