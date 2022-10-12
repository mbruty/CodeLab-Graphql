package net.bruty.CodeLabs.graphql.data

interface ICookieHandler {
    fun addCookie(name: String, data: String)
    fun removeCookie(name: String)
}