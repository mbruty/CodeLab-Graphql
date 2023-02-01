package net.bruty.CodeLabs.graphql.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.bruty.CodeLabs.graphql.annotations.Authenticate
import net.bruty.CodeLabs.graphql.data.CodeData
import net.bruty.CodeLabs.graphql.data.CodeResponse
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.repository.interfaces.ILanguageRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.IProgrammingTaskRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserCodeSubmissionRepository
import net.bruty.types.UserCodeSubmissionInput
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException.BadRequest
import org.springframework.web.client.RestTemplate


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

    @Autowired
    lateinit var codeRepository: IUserCodeSubmissionRepository

    @DgsQuery
    @Authenticate   
    fun evaluate(code: String, language: String, taskId: Int): CodeResponse? {
        CoroutineScope(Dispatchers.Default).launch {
            spinUpContainer(language);
        }
        val task = programmingTaskRepository.getStarterCodeByLanguage(taskId, language);
        val data = CodeData(code = code, test = task.testCode ?: throw NotFoundException(), file = task.includedFiles, file_name = task.fileName);
        val queue = languageRepository.getQueueNameByLanguage(language);
        val dataStr = Json.encodeToJsonElement(data).toString();
        val response = template.sendAndReceive(exchange.name, queue, Message(dataStr.toByteArray()));
        val body = response?.body ?: return null
        val res = Json.decodeFromString<CodeResponse>(String(body));
        return res;
    }

    @DgsMutation
    @Authenticate
    fun submitCode(submission: UserCodeSubmissionInput): Boolean {
        var isSuccess = true;
        try {
            codeRepository.upsert(submission);
        } catch (e: UnauthorisedException) {
            throw e;
        } catch (e: Exception) {
            e.printStackTrace()
            isSuccess = false;
        }
        return isSuccess;
    }

    private fun spinUpContainer(language: String) {
        val restTemplate = RestTemplate()

        val url = "http://scheduler.bruty.net/"
        val requestJson = "{\"language\":\"$language\"}"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val entity: HttpEntity<String> = HttpEntity<String>(requestJson, headers)
        val answer = restTemplate.postForObject(url, entity, String::class.java)
        println(answer)
    }
}