package net.bruty.comp3000graphql.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import net.bruty.comp3000graphql.repository.interfaces.IProgrammingTaskRepository
import net.bruty.types.ProgrammingTask
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class ProgrammingTaskDataFetcher {
    @Autowired
    lateinit var programmingTaskRepository: IProgrammingTaskRepository

    @DgsQuery
    fun programmingTask(taskId: Int, language: String): ProgrammingTask {
        return programmingTaskRepository.getStarterCodeByLanguage(taskId, language)
    }
}