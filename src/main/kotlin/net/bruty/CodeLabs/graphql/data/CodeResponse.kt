package net.bruty.CodeLabs.graphql.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CodeResponse(
    @SerialName("console_output")
    val consoleOutput: String,
    val output: String,
    val stats: List<Stat>,

    @SerialName("error_text")
    val errorText: String,

    @SerialName("is_successful")
    val isSuccessful: Boolean,

    @SerialName("execution_time_ms")
    val executionTimeMS: Long
)
