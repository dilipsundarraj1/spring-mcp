<!-- TOC -->
* [MCP in SpringBoot](#mcp-in-springboot)
* [MCP Server using WebMVC](#mcp-server-using-webmvc)
  * [Create a Tool using SpringAI — Exposing the RESTAPI Endpoints as a tools via MCP](#create-a-tool-using-springai--exposing-the-restapi-endpoints-as-a-tools-via-mcp)
    * [Product Service exposes bunch of REST APIs to retrieve the product details.](#product-service-exposes-bunch-of-rest-apis-to-retrieve-the-product-details)
    * [Transforming these endpoints as Tools using MCP in Spring AI](#transforming-these-endpoints-as-tools-using-mcp-in-spring-ai)
      * [@Tool and ToolParam Annotation](#tool-and-toolparam-annotation)
    * [Register the tools into the Spring Context](#register-the-tools-into-the-spring-context)
  * [MCP Server Tools API: Exposing Available Tools in the MCP Server](#mcp-server-tools-api-exposing-available-tools-in-the-mcp-server)
  * [Create a Tool using SpringAI — Exposing the DB Interaction as a tool](#create-a-tool-using-springai--exposing-the-db-interaction-as-a-tool)
      * [@Tool and ToolParam Annotation](#tool-and-toolparam-annotation-1)
    * [Register the tool into the Spring Context](#register-the-tool-into-the-spring-context)
      * [Explanation](#explanation)
<!-- TOC -->

# MCP in SpringBoot

# MCP Server using WebMVC

## Create a Tool using SpringAI — Exposing the RESTAPI Endpoints as a tools via MCP

### Product Service exposes a bunch of REST APIs to retrieve the product details.

- In this example, we have these two endpoints that this services exposes.

```kotlin

@RestController
@RequestMapping("/v1/products")
class ProductController(private val productService: ProductService) {

    /**
     * Retrieve a product by its ID.
     */
    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<Product> {
        val product = productService.getProductById(id)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Retrieve all products.
     */
    @GetMapping
    fun getAllProducts( @RequestParam(required = false) name: String?): ResponseEntity<List<Product>> {
        if (name != null) {
            val products = productService.getProductsByName(name)
            return ResponseEntity.ok(products)
        } else {
            val products = productService.getAllProducts()
            return ResponseEntity.ok(products)
        }
    }
}
```

### Transforming these endpoints as Tools using MCP in Spring AI

- There are handy annotations available in Spring AI module to turn any function into a tool using the below annotations.

#### @Tool and ToolParam Annotation

- The `@Tool` annotation designates a method as a tool that can be externally invoked. It acts as a marker and provides metadata about the tool. This includes a brief description of what the tool does.
- The `@ToolParam` annotation is used to provide metadata for the parameters of a tool method. It describes the purpose of the parameter and what value should be provided when the tool is invoked.


```kotlin
@RestController
@RequestMapping("/v2/products")
class ProductControllerV2(private val productService: ProductService) {

    /**
     * Retrieve a product by its ID.
     */
    @GetMapping("/{id}")
    @Tool(description = "Find a Product by ID")
    fun getProductById(
        @PathVariable
        @ToolParam(description = "Person ID") id: Long
    ): ResponseEntity<Product> {
        val product = productService.getProductById(id)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Retrieve all products.
     */
    @GetMapping
    @Tool(description = "Find all the products")
    fun getAllProducts(@ToolParam(description = "Product Name", required = false) @RequestParam name: String?): ResponseEntity<List<Product>> {
        if (name != null) {
            val products = productService.getProductsByName(name)
            return ResponseEntity.ok(products)
        } else {
            val products = productService.getAllProducts()
            return ResponseEntity.ok(products)
        }
    }
}
```

### Register the tools into the Spring Context

[Reference - Tool Config](mcp-webmvc/mcp-server/src/main/kotlin/com/llm/config/ToolConfig.kt)

- In this example , **ProductControllerV2** is a Spring bean that has tools configured in it.
  - We just need to add it to the Spring Context as a bean like below.
  
```kotlin

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
```

### application.yml—Config to run the MCP Server
- The MCP server is configured to run on port 8085 and the **sse-message-endpoint** is set to **/mcp/messages**.
```yaml
server:
  port: 8085
spring:
  ai:
    mcp:
      server:
        name: person-mcp-server
        version: 1.0.0
        type: SYNC
        sse-message-endpoint: /mcp/messages
```

- More info about properties is available in this link: [Spring MCP Server Config](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html#_configuration_properties)

### MCP Server Tools API: Exposing Available Tools in the MCP Server

- All the tools that are available in the MCP Server are available as part of the **ToolCallbackProvider** Spring bean.
- So in here we can access it and expose it as a REST API.

```kotlin
@RestController
class ToolsController(
    private val toolCallbackProvider: ToolCallbackProvider
) {
    @GetMapping("/tools")
    fun tools(): Tools {

        val toolList = toolCallbackProvider
            .toolCallbacks
            .map { it as ToolCallback }

        return Tools(toolList.size, toolList)
    }
}

data class Tools(
  val totalNoOfTools: Int,
  val tool: List<ToolCallback> = emptyList()
)

```

## Create a Tool using SpringAI — Exposing the DB functions as a tool

#### @Tool and ToolParam Annotation

- The `@Tool` annotation designates a method as a tool that can be externally invoked. It acts as a marker and provides metadata about the tool. This includes a brief description of what the tool does.
- The `@ToolParam` annotation is used to provide metadata for the parameters of a tool method. It describes the purpose of the parameter and what value should be provided when the tool is invoked.


```Kotlin
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
```

### Register the tool into the Spring Context

- Add the class that has the tools to the Spring Context as a bean like below.
- In this case, we are adding PersonTools into the Spring Context by addint the [PersonTools](mcp-webmvc/mcp-server/src/main/kotlin/com/llm/person/PersonTools.kt).

```kotlin

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
```
[Reference - Tool Config](mcp-webmvc/mcp-server/src/main/kotlin/com/llm/config/ToolConfig.kt)
