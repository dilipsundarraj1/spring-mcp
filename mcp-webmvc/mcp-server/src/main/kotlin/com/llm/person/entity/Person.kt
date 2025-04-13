package com.llm.person.entity

import jakarta.persistence.*

@Entity
class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    var age: Int = 0
    var nationality: String? = null

    @Enumerated(EnumType.STRING)
    var gender: Gender? = null


    constructor(id: Long?, name: String?, lastName: String?, age: Int, nationality: String?, gender: Gender?) {
        this.id = id
        this.name = name
        this.age = age
        this.nationality = nationality
        this.gender = gender
    }

    constructor()
}