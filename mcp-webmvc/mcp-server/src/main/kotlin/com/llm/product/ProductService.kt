package com.llm.product

import com.llm.product.entity.Product
import com.llm.product.jpa.ProductRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ProductService::class.java)
    }

    /**
     * Retrieve a product by its ID.
     */
    fun getProductById(id: Long): Product? {
        log.info("Retrieving product with ID: {}", id)
        return productRepository.findById(id).orElse(null)
    }

    /**
     * Retrieve all products.
     */
    fun getAllProducts(): List<Product> {
        log.info("Retrieving all products")
        return productRepository.findAll().toList()
    }

    /**
     * Create a new product.
     */
    fun createProduct(product: Product): Product {
        log.info("Creating new product: {}", product)
        return productRepository.save(product)
    }

    /**
     * Update an existing product.
     */
    fun updateProduct(id: Long, updatedProduct: Product): Product? {
        return productRepository.findById(id).map { existingProduct ->
            existingProduct.name = updatedProduct.name
            existingProduct.description = updatedProduct.description
            existingProduct.price = updatedProduct.price
            existingProduct.category = updatedProduct.category
            log.info("Updating product with ID: {}", id)
            productRepository.save(existingProduct)
        }.orElse(null)
    }

    /**
     * Delete a product by its ID.
     */
    fun deleteProduct(id: Long): Boolean {
        return if (productRepository.existsById(id)) {
            log.info("Deleting product with ID: {}", id)
            productRepository.deleteById(id)
            true
        } else {
            log.warn("Product with ID: {} not found for deletion", id)
            false
        }
    }

    fun getProductsByName(name: String): List<Product> {
        log.info("getProductsByName product with name: {}", name)
        return productRepository.findByNameContainingIgnoreCase(name)
    }
}
