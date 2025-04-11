package com.llm.controller

import org.springframework.ai.model.function.FunctionCallback
import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.ToolCallbacks
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ToolsController(
    private val toolCallbackProvider: ToolCallbackProvider
) {

    @GetMapping("/tools")
    fun tools(): Array<out FunctionCallback> {

        return toolCallbackProvider
            .toolCallbacks
    }


}