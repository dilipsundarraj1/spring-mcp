package com.llm.config

import com.llm.product.entity.Product
import com.llm.product.jpa.ProductRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Configuration
class ProductDataLoader {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(ProductDataLoader::class.java)
    }

    @Bean
    fun loadProductData(productRepository: ProductRepository): CommandLineRunner {
        return CommandLineRunner { args: Array<String?>? ->
            val product1 = Product(
                name = "Laptop",
                description = "High-performance laptop with 16GB RAM",
                price = 999.99,
                category = "Electronics"
            )

            val product2 = Product(
                name = "Smartphone",
                description = "Latest model smartphone with 5G",
                price = 699.99,
                category = "Electronics"
            )

            val product3 = Product(
                name = "Coffee Maker",
                description = "Professional grade coffee machine",
                price = 199.99,
                category = "Appliances"
            )

            productRepository.saveAll(listOf(product1, product2, product3))
            log.info("Product data loaded: {} ", productRepository.findAll())
        }
    }
}