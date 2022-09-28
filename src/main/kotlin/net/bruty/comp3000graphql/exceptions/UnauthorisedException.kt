package net.bruty.comp3000graphql.exceptions

import graphql.GraphQLException

class UnauthorisedException: GraphQLException("Unauthorised and attempting to access protected data") {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}