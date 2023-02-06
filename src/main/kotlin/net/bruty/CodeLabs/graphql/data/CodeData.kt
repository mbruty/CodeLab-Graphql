package net.bruty.CodeLabs.graphql.data

import kotlinx.serialization.Serializable

@Serializable
data class CodeData(
    val id: String,
    val code: String,
    val test: String,
    val file: String?,
    val file_name: String?
)