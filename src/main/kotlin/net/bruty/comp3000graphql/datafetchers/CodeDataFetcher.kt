package net.bruty.comp3000graphql.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import kotlinx.serialization.decodeFromString
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import kotlinx.serialization.json.Json
import net.bruty.comp3000graphql.data.CodeResponse


@DgsComponent
class CodeDataFetcher {
    @Autowired
    lateinit var template: RabbitTemplate

    @Autowired
    lateinit var exchange: DirectExchange

    @DgsQuery
    fun evaluate(code: String, language: String): CodeResponse? {
        val response = template.sendAndReceive(exchange.name, language, Message(code.toByteArray()))
        val body = response?.body ?: return null
        return Json.decodeFromString<CodeResponse>(String(body))
    }
}