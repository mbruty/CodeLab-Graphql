package net.bruty.CodeLabs.graphql.mocks

import net.bruty.CodeLabs.graphql.data.ICookieHandler

class MockCookieHandler: ICookieHandler {
    val cookies = mutableMapOf<String, String>()
    override fun addCookie(name: String, data: String) {
        cookies[name] = data
    }

    override fun getCookie(key: String): String? {
        return cookies[key]
    }

    override fun removeCookie(name: String) {
        cookies.remove(name)
    }
}