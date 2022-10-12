package net.bruty.CodeLabs.graphql.data

import kotlinx.serialization.Serializable

@Serializable
data class CodeData(
    val code: String,
    val test: String
)