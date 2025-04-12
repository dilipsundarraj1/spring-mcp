package com.llm.dtos

import jakarta.validation.constraints.NotBlank

data class UserInput(@NotBlank val prompt: String )
