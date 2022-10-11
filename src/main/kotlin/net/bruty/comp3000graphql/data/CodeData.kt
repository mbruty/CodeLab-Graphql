package net.bruty.comp3000graphql.data

import kotlinx.serialization.Serializable

@Serializable
data class CodeData(
    val code: String,
    val test: String
)