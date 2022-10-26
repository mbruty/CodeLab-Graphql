package net.bruty.CodeLabs.graphql

import net.bruty.CodeLabs.graphql.security.HttpContext
import org.springframework.amqp.core.DirectExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.annotation.RequestScope
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class AppConfig: WebMvcConfigurer {
    @Bean
    @RequestScope
    fun httpContext(): HttpContext {
        return HttpContext()
    }

    @Bean
    fun exchange(): DirectExchange? {
        return DirectExchange("") // Use the default exchange
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://code-lab.bruty.net")
            .allowCredentials(true)
    }

}