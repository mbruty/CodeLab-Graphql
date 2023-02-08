package net.bruty.CodeLabs.graphql

import net.bruty.CodeLabs.graphql.security.HttpContext
import org.springframework.amqp.core.DirectExchange
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.context.annotation.RequestScope
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration


@Configuration
class AppConfig: WebMvcConfigurer {
    @Value("\${production}")
    private val production: String? = null
    @Bean
    @RequestScope
    fun httpContext(): HttpContext {
        return HttpContext()
    }

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory? {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.setHostName("redis-13064.c250.eu-central-1-1.ec2.cloud.redislabs.com")
        redisStandaloneConfiguration.setPort(13064)
        redisStandaloneConfiguration.setPassword(RedisPassword.of("GjgXGvNUDhT0WBxLdbnRKAnKVPUuOJkR"))
        val jedisClientConfiguration: JedisClientConfiguration.JedisClientConfigurationBuilder = JedisClientConfiguration.builder()
        jedisClientConfiguration.connectTimeout(Duration.ofMillis(10_000))
        jedisClientConfiguration.usePooling()
        return JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build())
    }


    @Bean
    fun redisTemplate(): RedisTemplate<String, Any>? {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(jedisConnectionFactory()!!)
        return template
    }

    @Bean
    fun exchange(): DirectExchange? {
        return DirectExchange("") // Use the default exchange
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        println("Using configuration production: $production")
        registry.addMapping("/**")
            .allowedOrigins(if (production == "true") "http://code-lab.bruty.net/" else "http://localhost:3000")
            .allowCredentials(true)
    }

}