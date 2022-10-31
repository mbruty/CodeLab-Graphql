package net.bruty.CodeLabs.graphql.mocks

import net.bruty.CodeLabs.graphql.security.Security
import java.util.*

class MockSecurity: Security() {
    var accessExpirationSeconds = 1
    var refreshExpirationSeconds = 1

    override fun getExpirationTime(type: TOKEN_TYPE): Date {
        if (type == TOKEN_TYPE.ACCESS_TOKEN)
            return Date(Date().time + accessExpirationSeconds * 1000)
        if(type == TOKEN_TYPE.REFRESH_TOKEN) {
            return Date(Date().time + refreshExpirationSeconds * 1000)
        }

        // This should never happen, but if it does, this will invalidate the token
        return Date()
    }
}