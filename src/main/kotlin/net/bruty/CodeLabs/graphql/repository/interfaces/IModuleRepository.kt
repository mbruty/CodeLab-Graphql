package net.bruty.CodeLabs.graphql.repository.interfaces

import net.bruty.CodeLabs.graphql.model.ModuleEntity
import net.bruty.types.Module
import net.bruty.types.ProgrammingTask
import net.bruty.types.User

interface IModuleRepository: IUUIDRepository<ModuleEntity, Module> {
    fun getTasks(id: String): List<ProgrammingTask>;
    fun getCreatedBy(id: String): User;
    fun getCompletedPct(id: String, userId: String): Float;
    fun link(moduleID: String, taskID: String, userID: String);
    fun findEnrolled(userId: String): List<Module>;
}