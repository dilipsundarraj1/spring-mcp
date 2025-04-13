package com.llm.config

import com.llm.person.PersonTools
import com.llm.product.ProductControllerV2
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ToolConfig {

    @Bean
    fun tools(personTools: PersonTools,
              productController: ProductControllerV2
    ): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
            .toolObjects(personTools, productController)
            .build()
    }
}