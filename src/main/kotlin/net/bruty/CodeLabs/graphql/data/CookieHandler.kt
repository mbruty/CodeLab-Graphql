package net.bruty.CodeLabs.graphql.data

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData
import org.springframework.web.context.request.ServletWebRequest
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

class CookieHandler : ICookieHandler {

    val _response: HttpServletResponse?

    constructor(dfe: DgsDataFetchingEnvironment) {
        val request = dfe.getDgsContext().requestData as DgsWebMvcRequestData
        _response = (request.webRequest as ServletWebRequest).response
    }

    constructor(response: HttpServletResponse) {
        _response = response
    }

    override fun addCookie(name: String, data: String) {
        _response?.addHeader("Set-Cookie", "$name=$data; HttpOnly; Secure; SameSite=None")
    }

    override fun removeCookie(name: String) {
        val cookie = Cookie(name, "")
        cookie.maxAge = 0
        _response?.addCookie(cookie)
    }
}

