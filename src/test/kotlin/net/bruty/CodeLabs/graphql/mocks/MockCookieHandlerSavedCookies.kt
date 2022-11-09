package net.bruty.CodeLabs.graphql.mocks

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import net.bruty.CodeLabs.graphql.data.ICookieHandler
import javax.servlet.http.HttpServletRequest

class MockCookieHandlerSavedCookies: ICookieHandler {

    // Empty constructors here just to make the CookieHanlderFactory happy
    constructor(request: HttpServletRequest) { }

    constructor(dfe: DgsDataFetchingEnvironment) { }

    override fun addCookie(name: String, data: String) {
        cookies[name] = data
    }

    override fun getCookie(key: String): String? {
        return cookies[key]
    }

    override fun removeCookie(name: String) {
        cookies.remove(name)
    }

    companion object {
        val cookies = mutableMapOf<String, String>()

        fun reset() {
            cookies.clear()
        }
    }
}