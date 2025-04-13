package com.llm.tools

import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


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
