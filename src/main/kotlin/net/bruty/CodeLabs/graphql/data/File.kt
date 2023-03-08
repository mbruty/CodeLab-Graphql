package net.bruty.CodeLabs.graphql.data

import kotlinx.serialization.Serializable

@Serializable
data class File(
    val fileName: String,
    val fileText: String
)
