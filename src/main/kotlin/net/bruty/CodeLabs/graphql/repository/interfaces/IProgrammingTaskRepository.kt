package net.bruty.CodeLabs.graphql.repository.interfaces

import net.bruty.CodeLabs.graphql.model.ProgrammingTaskEntity
import net.bruty.types.ProgrammingTask

interface IProgrammingTaskRepository: IUUIDRepository<ProgrammingTaskEntity, ProgrammingTask> {
    fun getStarterCodeByLanguage(id: String, language: String): ProgrammingTask
    fun getStarterCodeDefault(id: String): ProgrammingTask
    fun getLanguagesFor(id: String): List<String>
}