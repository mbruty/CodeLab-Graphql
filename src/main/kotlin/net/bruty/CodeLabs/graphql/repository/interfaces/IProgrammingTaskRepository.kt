package net.bruty.CodeLabs.graphql.repository.interfaces

import net.bruty.CodeLabs.graphql.model.ProgrammingTaskEntity
import net.bruty.types.ProgrammingTask

interface IProgrammingTaskRepository: IIntIDRepository<ProgrammingTaskEntity, ProgrammingTask> {
    fun getStarterCodeByLanguage(id: Int, language: String): ProgrammingTask
    fun getStarterCodeDefault(id: Int): ProgrammingTask
    fun getLanguagesFor(id: Int): List<String>
}