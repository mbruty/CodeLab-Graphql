package net.bruty.CodeLabs.graphql.exceptions

import graphql.GraphQLException

class NotFoundException: GraphQLException() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}