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
import net.bruty.CodeLabs.graphql.data.File
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.model.TaskQueueObject
import net.bruty.CodeLabs.graphql.repository.interfaces.ILanguageRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.IProgrammingTaskRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserCodeSubmissionRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.TaskRepository
import net.bruty.types.UserCodeSubmissionInput
import org.jetbrains.exposed.sql.transactions.transaction
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

    @Autowired
    lateinit var userCodeSubmissionRepository: IUserCodeSubmissionRepository

    @DgsQuery
    fun evaluateTest(code: String, testCode: String, language: String, files: List<File>): CodeResponse? {
        val data = CodeData(
            id = UUID.randomUUID().toString(),
            code = code, test = testCode,
            files
        );

        return executeCode(data, language);
    }
    @DgsQuery
    @Authenticate
    fun evaluate(code: String, language: String, taskId: String): CodeResponse? {
        val task = programmingTaskRepository.getStarterCodeByLanguage(taskId, language);
        val data = CodeData(
            id = UUID.randomUUID().toString(),
            code = code,
            test = task.code?.testCode ?: throw NotFoundException(),
            files = task.files?.map { File(fileName = it.fileName, fileText = it.fileText) } ?: emptyList()
        );

        val executionResponse = executeCode(data, language);
        if (executionResponse?.isSuccessful == true) {
            codeRepository.upsert(
                UserCodeSubmissionInput(
                    codeText = code,
                    language = language,
                    taskId = taskId,
                    isCompleted = true
                )
            )
        }
        return executionResponse
    }

    fun executeCode(data: CodeData, language: String): CodeResponse? {
        val taskObject = TaskQueueObject(id = data.id, retryCount = 0);
        taskRepository.save(taskObject);

        val queue = languageRepository.getQueueNameByLanguage(language);
        val dataStr = Json.encodeToJsonElement(data).toString();
        // The task shouldn't run for more than 10 seconds, and a build shouldn't take more than 10 seconds.
        // Then allow 10 seconds in the queue
        // So timeout after 30 seconds
        template.setReplyTimeout(30_000);
        val response = template.sendAndReceive(exchange.name, queue, Message(dataStr.toByteArray()));
        val body = response?.body ?: return null
        val strBody = String(body)
        if (strBody == "Max retry hit") {
            return CodeResponse(
                errorText = "The code failed to execute 3 times without providing an error.",
                isSuccessful = false,
                executionTimeMS = 0,
                output = "The code failed to execute 3 times without providing an error.",
                consoleOutput = "",
                stats = emptyList()
            );
        }

        if (strBody.contains("Process timed out"))  {
            return CodeResponse(
                errorText = strBody,
                isSuccessful = false,
                executionTimeMS = 0,
                output = strBody,
                consoleOutput = "",
                stats = emptyList()
            );
        }
        val res = Json.decodeFromString<CodeResponse>(strBody);
        return res;
    }
    @DgsMutation
    @Authenticate
    fun submitCode(submission: UserCodeSubmissionInput): Boolean {
        var isSuccess = true;
        try {
            codeRepository.upsert(
                // If the code has changed, we assume that it's not completed
                submission.copy(isCompleted = false)
            );
        } catch (e: UnauthorisedException) {
            throw e;
        } catch (e: Exception) {
            e.printStackTrace()
            isSuccess = false;
        }
        return isSuccess;
    }
}