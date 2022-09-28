package net.bruty.comp3000graphql.data

interface ICookieHandler {
    fun addCookie(name: String, data: String)
    fun removeCookie(name: String)
}