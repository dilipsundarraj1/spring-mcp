# MCP in SpringBoot 

## MCP Server using WebMVC

### Create a Tool using SpringAI - Exposing the DB as a tool



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

```kotlin

@Bean
    fun tools(personTools: PersonTools): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
            .toolObjects(personTools)
            .build()
    }

```

### Explanation

- `@Bean` Annotation: Marks the method as a bean producer.
  - Spring will execute this method and register its return value as a bean in the application context.
- Method Parameter: `personTools` is automatically injected by Spring. It's an instance of your PersonTools class.
  
- Return Value: The method returns a ToolCallbackProvider configured with your personTools instance. This setup allows the framework to recognize and utilize the tool methods defined in PersonTools.
  
- By registering your tool in this manner, you ensure it's managed by Spring and available for dependency injection throughout your application.