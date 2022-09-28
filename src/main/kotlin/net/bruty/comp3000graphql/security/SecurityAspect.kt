package net.bruty.comp3000graphql.security

import net.bruty.comp3000graphql.data.CookieHandlerFactory
import net.bruty.comp3000graphql.exceptions.UnauthorisedException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Aspect
@Component
class SecurityAspect {

    @Autowired
    private lateinit var _ctx: HttpContext

    @Autowired
    private lateinit var _security: Security


    @Pointcut("@annotation(net.bruty.comp3000graphql.annotations.Authenticate)")
    private fun securityAnnotation() {
    }

    @Around("net.bruty.comp3000graphql.security.SecurityAspect.securityAnnotation()")
    fun checkAuth(pjp: ProceedingJoinPoint): Any {
        val req = request

        if(req.cookies == null) throw UnauthorisedException()

        req.cookies
        val accessToken: String = req.cookies.find { it.name == "access_token" }?.value ?: throw UnauthorisedException()
        val refreshToken: String = req.cookies.find { it.name == "refresh_token" }?.value ?: throw UnauthorisedException()

        _ctx.principal = _security.getPrincipal(accessToken, refreshToken, CookieHandlerFactory.getHandler(response))

        return pjp.proceed()
    }

    private val request: HttpServletRequest
        get() {
            val sra = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
            return sra!!.request
        }

    private val response: HttpServletResponse
        get() {
            val sra = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
            return sra!!.response!!
        }
}