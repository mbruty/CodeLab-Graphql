package net.bruty.CodeLabs.graphql.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.bruty.CodeLabs.graphql.annotations.Authenticate
import net.bruty.CodeLabs.graphql.data.CodeData
import net.bruty.CodeLabs.graphql.data.CodeResponse
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.model.TaskQueueObject
import net.bruty.CodeLabs.graphql.repository.interfaces.ILanguageRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.IProgrammingTaskRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserCodeSubmissionRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.TaskRepository
import net.bruty.types.UserCodeSubmissionInput
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

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

    @Autowired
    lateinit var taskRepository: TaskRepository

    @DgsQuery
    @Authenticate
    fun evaluate(code: String, language: String, taskId: Int): CodeResponse? {
        val task = programmingTaskRepository.getStarterCodeByLanguage(taskId, language);
        val data = CodeData(id = UUID.randomUUID().toString(), code = code, test = task.testCode ?: throw NotFoundException(), file = task.includedFiles, file_name = task.fileName);

        val taskObject = TaskQueueObject(id = data.id, retryCount = 0);
        taskRepository.save(taskObject);

        val queue = languageRepository.getQueueNameByLanguage(language);
        val dataStr = Json.encodeToJsonElement(data).toString();
        val response = template.sendAndReceive(exchange.name, queue, Message(dataStr.toByteArray()));
        val body = response?.body ?: return null
        if (String(body) == "Max retry hit") {
            return CodeResponse(
                errorText = "The code failed to execute 3 times without providing an error.",
                isSuccessful = false,
                executionTimeMS = 0,
                output = "The code failed to execute 3 times without providing an error.",
                stats = emptyList()
            );
        }
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
}