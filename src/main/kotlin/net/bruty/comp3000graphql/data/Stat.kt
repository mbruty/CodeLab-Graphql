package net.bruty.comp3000graphql.data

import kotlinx.serialization.Serializable

@Serializable
data class Stat (
    val cpu: String,
    val mem: String
)