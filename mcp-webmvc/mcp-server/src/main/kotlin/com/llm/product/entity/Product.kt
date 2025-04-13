package com.llm.product.entity

import jakarta.persistence.*

@Entity
class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var name: String? = null

    var description: String? = null

    var price: Double = 0.0

    var category: String? = null

    // Primary constructor for creating a new product record
    constructor(name: String?, description: String?, price: Double, category: String?) {
        this.name = name
        this.description = description
        this.price = price
        this.category = category
    }

    // Default constructor required by JPA
    constructor()

    override fun toString(): String {
        return "Product(id=$id, name=$name, description=$description, price=$price, category=$category)"
    }
}
