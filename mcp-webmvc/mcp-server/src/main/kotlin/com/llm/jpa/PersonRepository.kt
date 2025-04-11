package com.llm.jpa

import com.llm.entity.Person
import org.springframework.data.repository.CrudRepository

interface PersonRepository : CrudRepository<Person, Long> {

    fun findByNationality(nationality: String?): List<Person>?

}