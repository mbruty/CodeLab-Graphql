package net.bruty.CodeLabs.graphql.data

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

class CookieHandlerFactory {
    companion object {
        var factoryClass: KClass<ICookieHandler> = CookieHandler::class as KClass<ICookieHandler>

        fun getHandler(dfe: DgsDataFetchingEnvironment): ICookieHandler {
            for (constructor in factoryClass.constructors) {
                if (constructor.parameters[0].type.jvmErasure == DgsDataFetchingEnvironment::class) {
                    return constructor.call(dfe)
                }
            }
            // Fall back to concrete implementation
            return CookieHandler(dfe)
        }

        fun getHandler(response: HttpServletResponse): ICookieHandler {
            for (constructor in factoryClass.constructors) {
                if (constructor.parameters[0].type.jvmErasure == HttpServletResponse::class) {
                    return constructor.call(response)
                }
            }
            // Fall back to concrete implementation
            return CookieHandler(response)
        }

        fun getHandler(request: HttpServletRequest): ICookieHandler {
            for (constructor in factoryClass.constructors) {
                if (constructor.parameters[0].type.jvmErasure == HttpServletRequest::class) {
                    return constructor.call(request)
                }
            }
            // Fall back to concrete implementation
            return CookieHandler(request)
        }
    }
}
