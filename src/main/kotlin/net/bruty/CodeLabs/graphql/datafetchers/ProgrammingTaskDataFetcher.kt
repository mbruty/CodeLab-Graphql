package net.bruty.CodeLabs.graphql.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery
import net.bruty.CodeLabs.graphql.annotations.Authenticate
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.extensions.toUUID
import net.bruty.CodeLabs.graphql.model.ProgrammingTaskEntity
import net.bruty.CodeLabs.graphql.model.ProgrammingTaskStarterCodeEntity
import net.bruty.CodeLabs.graphql.model.ProgrammingTaskStarterCodeTable
import net.bruty.CodeLabs.graphql.model.ProgrammingTaskTable
import net.bruty.CodeLabs.graphql.repository.interfaces.IProgrammingTaskRepository
import net.bruty.CodeLabs.graphql.security.HttpContext
import net.bruty.types.File
import net.bruty.types.ProgrammingTask
import net.bruty.types.ProgrammingTaskCode
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class ProgrammingTaskDataFetcher {
    @Autowired
    lateinit var programmingTaskRepository: IProgrammingTaskRepository

    @Autowired
    lateinit var httpContext: HttpContext

    @DgsQuery
    fun programmingTask(taskId: String): ProgrammingTask {
        return programmingTaskRepository.findByIdOrThrow(taskId).toDTO()
    }

    @DgsData(parentType = "ProgrammingTask")
    fun availableLanguages(dfe: DgsDataFetchingEnvironment): List<String> {
        val task = dfe.getSource<ProgrammingTask>();
        val id = task.id.split(".")[0];
        return programmingTaskRepository.getLanguagesFor(id);
    }

    @DgsData(parentType = "ProgrammingTask")
    @Authenticate
    fun code(language: String, dfe: DgsDataFetchingEnvironment): ProgrammingTaskCode {
        val task = dfe.getSource<ProgrammingTask>()
        val userId = httpContext.principal?.userUUID ?: throw UnauthorisedException()
        return transaction {
            val foundTask = ProgrammingTaskEntity.findById(task.id.toUUID()) ?: throw NotFoundException()
            val code = if (language == "default") {
                foundTask.startCodes.firstOrNull { it.language == foundTask.defaultLanguage }
            } else {
                foundTask.startCodes.firstOrNull { it.language.language == language }
            }

            if (code == null) throw NotFoundException()

            ProgrammingTaskCode(
                starterCode = code.starterCode,
                testCode = code.unitTestCode,
                myCode = (foundTask.userSubmissions.firstOrNull { it.createdBy.id.value ==  userId }?.codeText) ?: code.starterCode,
                language = transaction { code.language.language }
            )
        }
    }

    @DgsData(parentType = "ProgrammingTask")
    fun files(dfe: DgsDataFetchingEnvironment): List<File> {
        return transaction {
            val task = ProgrammingTaskEntity.findById(dfe.getSource<ProgrammingTask>().id.toUUID()) ?: throw NotFoundException()
            task.files.map { File(fileText = it.fileText, fileName = it.fileName) }
        }
    }
}