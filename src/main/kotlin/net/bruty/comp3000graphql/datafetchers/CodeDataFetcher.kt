package net.bruty.comp3000graphql.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery
import kotlinx.serialization.decodeFromString
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.bruty.comp3000graphql.data.CodeData
import net.bruty.comp3000graphql.data.CodeResponse
import net.bruty.comp3000graphql.repository.interfaces.ILanguageRepository
import net.bruty.comp3000graphql.repository.interfaces.IProgrammingTaskRepository


@DgsComponent
class CodeDataFetcher {
    @Autowired
    lateinit var template: RabbitTemplate

    @Autowired
    lateinit var exchange: DirectExchange

    @Autowired
    lateinit var programmingTaskRepository: IProgrammingTaskRepository

    @Autowired
    lateinit var languageRepository: ILanguageRepository

    @DgsQuery
    fun evaluate(code: String, language: String, taskId: Int): CodeResponse? {
        val task = programmingTaskRepository.getStarterCodeByLanguage(taskId, language);
        val data = CodeData(code = code, test = task.testCode);
        val queue = languageRepository.getQueueNameByLanguage(language);
        val dataStr = Json.encodeToJsonElement(data).toString();
        val response = template.sendAndReceive(exchange.name, queue, Message(dataStr.toByteArray()));
        val body = response?.body ?: return null
        val res = Json.decodeFromString<CodeResponse>(String(body));
        return res;
    }
}