package net.bruty.CodeLabs.graphql.security

import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema
import java.util.UUID
@JsonSerializableSchema
interface IPrincipal {
    val userId: String
    val userUUID: UUID get() = UUID.fromString(userId)
    val refreshCount: Int
}