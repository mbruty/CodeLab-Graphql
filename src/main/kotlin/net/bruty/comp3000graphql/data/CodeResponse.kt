package net.bruty.comp3000graphql.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CodeResponse(
    val output: List<String>,
    val stats: List<Stat>,

    @SerialName("execution_time_ms")
    val executionTimeMS: Long
)
