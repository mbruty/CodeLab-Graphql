package net.bruty.CodeLabs.graphql.extensions
import java.util.UUID
fun String.toUUID(): UUID {
    return UUID.fromString(this)
}