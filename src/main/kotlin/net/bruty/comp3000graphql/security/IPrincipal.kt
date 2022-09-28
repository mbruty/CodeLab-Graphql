package net.bruty.comp3000graphql.security

import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema

@JsonSerializableSchema
interface IPrincipal {
    val userId: Int
    val refreshCount: Int
}