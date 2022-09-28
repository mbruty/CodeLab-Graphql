package net.bruty.comp3000graphql.exceptions

import graphql.GraphQLException

class NotFoundException: GraphQLException() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}