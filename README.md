<!-- TOC -->
* [MCP using SpringBoot](#mcp-using-springboot)
* [MCP Server using SpringAI - WebMVC](#mcp-server-using-springai---webmvc)
  * [Create a Tool using SpringAI — Exposing the RESTAPI Endpoints as a tools via MCP](#create-a-tool-using-springai--exposing-the-restapi-endpoints-as-a-tools-via-mcp)
    * [Product Service exposes a bunch of REST APIs to retrieve the product details.](#product-service-exposes-a-bunch-of-rest-apis-to-retrieve-the-product-details)
    * [How to transform these endpoints as Tools using MCP in Spring AI ?](#how-to-transform-these-endpoints-as-tools-using-mcp-in-spring-ai-)
      * [@Tool and ToolParam Annotation](#tool-and-toolparam-annotation)
    * [Register the tools into the Spring Context](#register-the-tools-into-the-spring-context)
    * [application.yml—Config to run the MCP Server](#applicationymlconfig-to-run-the-mcp-server)
    * [Start the MCP Server](#start-the-mcp-server)
    * [MCP Server Tools API: Exposing Available Tools in the MCP Server](#mcp-server-tools-api-exposing-available-tools-in-the-mcp-server)
  * [Create a Tool using SpringAI — Exposing the DB functions as a tool](#create-a-tool-using-springai--exposing-the-db-functions-as-a-tool)
      * [@Tool and ToolParam Annotation](#tool-and-toolparam-annotation-1)
    * [Register the tool into the Spring Context](#register-the-tool-into-the-spring-context)
    * [Code Reference](#code-reference)
* [MCP Client using SpringAI - WebMVC](#mcp-client-using-springai---webmvc)
    * [application.yml - Config to enable the MCP Client](#applicationyml---config-to-enable-the-mcp-client)
    * [application.yml - Config Properties](#applicationyml---config-properties)
    * [Configuring MCP Servers into the App](#configuring-mcp-servers-into-the-app)
    * [Invoking the LLM by passing the tools.](#invoking-the-llm-by-passing-the-tools)
    * [Start the MCP Client](#start-the-mcp-client)
    * [Code Reference](#code-reference-1)
<!-- TOC -->

# MCP using SpringBoot

# MCP Server using SpringAI - WebMVC


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

### How to transform these endpoints as Tools using MCP in Spring AI ?

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

### Start the MCP Server
- The MCP server is a Spring Boot application and can be started like any other Spring Boot application.
[Start MCPServer](mcp-webmvc/mcp-server/src/main/kotlin/com/llm/MCPServer.kt)


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

### Code Reference
[MCP Server using Spring AI](mcp-webmvc/mcp-server)

# MCP Client using SpringAI - WebMVC

- The MCP Client is used to connect to the MCP Server and retrieve the tools that are available in the server.
- It can also invoke the tools if there a tool can fulfill the user query.

### application.yml - Config to enable the MCP Client

```yaml
spring:
  ai:
    mcp:
      client:
        enabled: true
        type: SYNC
        initialized: true
        request-timeout: 240s
        sse:
          connections:
            person-mcp-server:
              url: http://localhost:8085
    openai:
      api-key: ${OPENAI_KEY}
      chat:
        enabled: true # By default, it's true, this triggers the autoconfiguration.
        options:
          model: gpt-4o
          temperature: 0.7
          max_completion_tokens: 2000
          internal-tool-execution-enabled: true
```
### application.yml - Config Properties

-  **SYNC** - The client is configured to use synchronous communication with the MCP server.
-  **initialized** - The client is initialized to connect to the MCP server.
- **sse.connections:**
  - This app can connect to multiple MCP Servers and the connection name is used to identify the server.

- More info about properties is available in this link: [Spring MCP Client Config](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-client-boot-starter-docs.html#_configuration_properties)

### Configuring MCP Servers into the App

```kotlin

@RestController
class ChatsController(
    chatClientBuilder: ChatClient.Builder,
    mcpClients: List<McpSyncClient>
) {

    private val chatClient: ChatClient = chatClientBuilder
        .defaultTools(SyncMcpToolCallbackProvider(mcpClients))
        .build()

}

```

- ChatClient
  -  This is the main client that is used to connect to the MCP server and retrieve the tools that are available in the server.
  - This will invoke the tool if the user prompt requires a tool invocation to get the result.
- defaultTools
  -  This method is used to set the default tools that are available in the MCP server.
- McpSyncClient
  - This instance holds the MCP server connection and is used to connect to the MCP server.

### Invoking the LLM by passing the tools.

- Using the **chatClient** , we can invoke the LLM by passing the tools that are available in the MCP server.
- If a specific call requires a tool invocation, the LLM will invoke the tool and return the result.

```kotlin

@PostMapping("/v1/chats")
    fun chat(@RequestBody @Valid userInput: UserInput): String? {
        log.info("userInput message : {} ", userInput)
        val requestSpec: ChatClient.ChatClientRequestSpec =
            chatClient.prompt()
                .user(userInput.prompt)

        log.info("requestSpec : {} ", requestSpec)
        val responseSpec = requestSpec.call()
        log.info("responseSpec1 : {} ", responseSpec)
        log.info("content : {} ", responseSpec.content())
        return responseSpec.content()
    }
```

### Start the MCP Client
- The MCP Clent is a Spring Boot application and can be started like any other Spring Boot application.
  [Start MCPClient](mcp-webmvc/mcp-client/src/main/kotlin/com/llm/MCPClient.kt)


### Code Reference
[MCP Client using Spring AI](mcp-webmvc/mcp-client)
