package net.bruty.CodeLabs.graphql.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import net.bruty.CodeLabs.graphql.data.ICookieHandler
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class Security {

    @Autowired
    private lateinit var _ctx: HttpContext

    @Autowired
    private lateinit var _userRepository: IUserRepository

    companion object {
        val key: SecretKey = Keys.hmacShaKeyFor("mXa09yfjBDA00ByV7KMsla8dwOt8Q6zyaCEEgxOPGvETh8lpSGuJIdecettPd00lyd7TIevHvUmg4qKflhVJOrqdeFUclikmn2q".toByteArray())
    }

    protected enum class TOKEN_TYPE(val key: String) {
        ACCESS_TOKEN("access_token"),
        REFRESH_TOKEN("refresh_token")
    }

    protected fun getExpirationTime(type: TOKEN_TYPE): Date {
        // Access token valid for 15 mins
        if (type == TOKEN_TYPE.ACCESS_TOKEN)
            return Date(Date().time + (15 * 60 * 1000))
        // Refresh token is valid for 1 year, or when the user signs out
        if(type == TOKEN_TYPE.REFRESH_TOKEN) {
            return Date(Date().time + 31556952000)
        }

        // We shouldn't get here, so just return now to automatically make it invalid
        return Date()
    }

    /**
     * Creates an access token, and refresh token
     * @param user The user you wish to create the access token for
     * @return Array { accessToken, refreshToken }
     */
    fun setTokens(user: IPrincipal, cookieHandler: ICookieHandler): List<String> {
        val userJson = ObjectMapper().writeValueAsString(user)

        val accessToken: String = Jwts.builder()
            .setSubject(userJson)
            .setIssuer("tastebuds.com")
            .setExpiration(getExpirationTime(TOKEN_TYPE.ACCESS_TOKEN))
            .setIssuedAt(Date())
            .signWith(key)
            .compact()

        val refreshToken: String = Jwts.builder()
            .setSubject(userJson)
            .setIssuer("tastebuds.com")
            .setExpiration(getExpirationTime(TOKEN_TYPE.REFRESH_TOKEN))
            .setIssuedAt(Date())
            .signWith(key)
            .compact()

        cookieHandler.addCookie("access_token", accessToken)
        cookieHandler.addCookie("refresh_token", refreshToken)

        return listOf(accessToken, refreshToken)
    }

    fun processToken(token: String): IPrincipal? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
            ObjectMapper().readValue(claims.subject, UserPrincipal::class.java)
        }
        catch (e: ExpiredJwtException) { null }
        catch (e: Exception) { throw UnauthorisedException() }
    }

    fun getPrincipal(accessToken: String, refreshToken: String, cookieHandler: ICookieHandler): IPrincipal {
        val access = processToken(accessToken)
        // It worked
        if(access != null) return access

        val refresh = processToken(refreshToken) ?: throw UnauthorisedException()

        // If we get here, access was invalid, refresh was valid
        // So refresh both tokens

        // Get the current user and check the refresh count.
        // Refresh count is used to invalidate long-standing tokens by being incremented
        val user = _userRepository.findByIdOrThrow(refresh.userId)

        if(user.refreshCount != refresh.refreshCount) throw UnauthorisedException()

        setTokens(refresh, cookieHandler)
        return refresh
    }

    fun logOut(cookieHandler: ICookieHandler) {
        cookieHandler.removeCookie("access_token")
        cookieHandler.removeCookie("refresh_token")
    }
}