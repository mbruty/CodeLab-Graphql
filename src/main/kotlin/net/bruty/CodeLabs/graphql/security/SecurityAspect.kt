package net.bruty.CodeLabs.graphql.security

import net.bruty.CodeLabs.graphql.data.CookieHandlerFactory
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
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


    @Pointcut("@annotation(net.bruty.CodeLabs.graphql.annotations.Authenticate)")
    private fun securityAnnotation() {
    }

    @Around("net.bruty.CodeLabs.graphql.security.SecurityAspect.securityAnnotation()")
    fun checkAuth(pjp: ProceedingJoinPoint): Any {
        val req = request
        val handler = CookieHandlerFactory.getHandler(request);

        val accessToken = handler.getCookie("access_token") ?: throw UnauthorisedException()
        val refreshToken = handler.getCookie("refresh_token") ?: throw UnauthorisedException()

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