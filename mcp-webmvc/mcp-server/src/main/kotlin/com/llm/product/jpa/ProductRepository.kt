package com.llm.product.jpa

import com.llm.product.entity.Product
import org.springframework.data.repository.CrudRepository

interface ProductRepository : CrudRepository<Product, Long> {

    fun findByNameContainingIgnoreCase(name: String): List<Product>

}
