package net.bruty.CodeLabs.graphql.exceptions

import graphql.GraphQLException

class AlreadyExistsException: GraphQLException("Item already exists") {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}