package net.bruty.CodeLabs.graphql.repository.interfaces

import net.bruty.CodeLabs.graphql.model.ModuleEntity
import net.bruty.types.Module
import net.bruty.types.ProgrammingTask
import net.bruty.types.User

interface IModuleRepository: IIntIDRepository<ModuleEntity, Module> {
    fun getTasks(id: Int): List<ProgrammingTask>;
    fun getCreatedBy(id: Int): User;
    fun getCompletedPct(id: Int, userId: Int): Float;
    fun link(moduleID: Int, taskID: Int, userID: Int);
    fun findEnrolled(userId: Int): List<Module>;
}