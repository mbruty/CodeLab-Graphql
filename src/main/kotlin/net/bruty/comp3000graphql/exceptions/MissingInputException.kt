package net.bruty.comp3000graphql.exceptions

import graphql.GraphQLException

class MissingInputException(message: String): GraphQLException(message) {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}