package net.bruty.CodeLabs.graphql.datafetchers

import com.netflix.graphql.dgs.*
import net.bruty.CodeLabs.graphql.annotations.Authenticate
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.model.ModuleEntity
import net.bruty.CodeLabs.graphql.repository.interfaces.IModuleRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserRepository
import net.bruty.CodeLabs.graphql.security.HttpContext
import net.bruty.types.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@DgsComponent
class ModuleDataFetcher {

    @Autowired
    lateinit var moduleRepository: IModuleRepository

    @Autowired
    lateinit var userRepository: IUserRepository

    @Autowired
    lateinit var httpContext: HttpContext

    @DgsMutation
    fun createTask(
        title: String,
        description: String,
        starterCodes: Array<StarterCodeInput>,
        files: Array<FileInput>?
    ): Boolean {
        return true
    }

    @DgsQuery
    fun module(moduleId: String): Module {
        val module = moduleRepository.findByIdOrThrow(moduleId);
        return Module(
            title = module.title,
            description = module.description,
            id = module.id.value.toString(),
            completedPct = -1.0
        )
    }

    @DgsQuery
    @Authenticate
    fun myModules(): List<Module> {
        val userId = httpContext.principal?.userId ?: throw UnauthorisedException()
        return moduleRepository.findEnrolled(userId);
    }

    @DgsQuery
    @Authenticate
    fun editableModules(): List<Module> {
        val userId = httpContext.principal?.userId ?: throw UnauthorisedException()
        val myModules = moduleRepository.findEnrolled(userId);
        return myModules.filter { it.createdBy?.id == userId }
    }

    @DgsData(parentType = "Module")
    fun tasks(dfe: DgsDataFetchingEnvironment): List<ProgrammingTask> {
        val module = dfe.getSource<Module>();
        return moduleRepository.getTasks(module.id)
    }

    @DgsData(parentType = "Module")
    fun createdBy(dfe: DgsDataFetchingEnvironment): User {
        val module = dfe.getSource<Module>();
        return moduleRepository.getCreatedBy(module.id);
    }

    @DgsData(parentType = "Module")
    @Authenticate
    fun completedPct(dfe: DgsDataFetchingEnvironment): Float {
        val userID = httpContext.principal?.userId ?: throw UnauthorisedException();
        val module = dfe.getSource<Module>();
        return moduleRepository.getCompletedPct(module.id, userID)
    }

    @DgsData(parentType = "Module")
    @Authenticate
    fun canEdit(dfe: DgsDataFetchingEnvironment): Boolean {
        val userID = httpContext.principal?.userUUID ?: throw UnauthorisedException();
        val module = dfe.getSource<Module>();
        return transaction {
            val m = ModuleEntity.findById(UUID.fromString(module.id));
            return@transaction m?.createdBy?.id?.value == userID;
        }
    }

    @DgsMutation
    @Authenticate
    fun createModule(
        @InputArgument moduleTitle: String,
        @InputArgument moduleDescription: String
    ): Boolean {
        val user = userRepository.findById(
            httpContext.principal?.userId ?: throw UnauthorisedException()
        ) ?: throw UnauthorisedException()

        val module = Module(
            id = "",
            title = moduleTitle,
            description = moduleDescription,
            createdBy = user.toModel(),
            completedPct = -1.0
        )
        moduleRepository.create(module)
        return true
    }

    @DgsMutation
    @Authenticate
    fun linkModuleTask(moduleID: String, taskID: String): Boolean {
        moduleRepository.link(moduleID, taskID, httpContext.principal!!.userId)
        return true
    }
}