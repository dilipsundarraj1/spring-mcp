package com.llm.person.jpa

import com.llm.person.entity.Person
import org.springframework.data.repository.CrudRepository

interface PersonRepository : CrudRepository<Person, Long> {

    fun findByNationality(nationality: String?): List<Person>?

}