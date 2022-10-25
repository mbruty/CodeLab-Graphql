package net.bruty.CodeLabs.graphql.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery
import net.bruty.CodeLabs.graphql.annotations.Authenticate
import net.bruty.CodeLabs.graphql.repository.interfaces.IProgrammingTaskRepository
import net.bruty.types.ProgrammingTask
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class ProgrammingTaskDataFetcher {
    @Autowired
    lateinit var programmingTaskRepository: IProgrammingTaskRepository

    @DgsQuery
    @Authenticate
    fun programmingTask(taskId: Int, language: String): ProgrammingTask {
        if(language == "default") {
            return programmingTaskRepository.getStarterCodeDefault(taskId);
        }
        return programmingTaskRepository.getStarterCodeByLanguage(taskId, language)
    }

    @DgsData(parentType = "ProgrammingTask")
    fun availableLanguages(dfe: DgsDataFetchingEnvironment): List<String> {
        val task = dfe.getSource<ProgrammingTask>();
        val id = task.id.split(".")[0].toInt();
        return programmingTaskRepository.getLanguagesFor(id);
    }
}