package net.bruty.CodeLabs.graphql.datafetchers

import com.netflix.graphql.dgs.*
import net.bruty.CodeLabs.graphql.annotations.Authenticate
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.repository.interfaces.IModuleRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserRepository
import net.bruty.CodeLabs.graphql.security.HttpContext
import net.bruty.types.Module
import net.bruty.types.ProgrammingTask
import net.bruty.types.User
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class ModuleDataFetcher {

    @Autowired
    lateinit var moduleRepository: IModuleRepository

    @Autowired
    lateinit var userRepository: IUserRepository

    @Autowired
    lateinit var httpContext: HttpContext

    @DgsQuery
    fun module(moduleId: Int): Module {
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

    @DgsData(parentType = "Module")
    fun tasks(dfe: DgsDataFetchingEnvironment): List<ProgrammingTask> {
        val module = dfe.getSource<Module>();
        return moduleRepository.getTasks(module.id.toInt())
    }

    @DgsData(parentType = "Module")
    fun createdBy(dfe: DgsDataFetchingEnvironment): User {
        val module = dfe.getSource<Module>();
        return moduleRepository.getCreatedBy(module.id.toInt());
    }

    @DgsData(parentType = "Module")
    @Authenticate
    fun completedPct(dfe: DgsDataFetchingEnvironment): Float {
        val userID = httpContext.principal?.userId ?: throw UnauthorisedException();
        val module = dfe.getSource<Module>();
        return moduleRepository.getCompletedPct(module.id.toInt(), userID)
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
    fun linkModuleTask(moduleID: Int, taskID: Int): Boolean {
        moduleRepository.link(moduleID, taskID, httpContext.principal!!.userId)
        return true
    }
}