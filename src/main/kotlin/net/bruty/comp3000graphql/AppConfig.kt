package net.bruty.comp3000graphql

import net.bruty.comp3000graphql.security.HttpContext
import org.springframework.amqp.core.DirectExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.annotation.RequestScope


@Configuration
class AppConfig {
    @Bean
    @RequestScope
    fun httpContext(): HttpContext {
        return HttpContext()
    }

    @Bean
    fun exchange(): DirectExchange? {
        return DirectExchange("") // Use the default exchange
    }
}