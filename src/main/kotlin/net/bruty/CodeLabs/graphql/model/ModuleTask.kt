package net.bruty.CodeLabs.graphql.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object ModuleTaskTable: IntIdTable() {
    val module = reference("module", ModuleTable, ReferenceOption.CASCADE)
    val task = reference("task", ProgrammingTaskTable, ReferenceOption.CASCADE)
    val order: Column<Int> = integer("order")

    init {
        // Add a unique index for:
        // One module can only have a task in once
        index(true, module, task)
    }
}
class ModuleTaskEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ModuleTaskEntity>(ModuleTaskTable)
    val module by ModuleTaskTable.module
    val task by ModuleTaskTable.task
    val order by ModuleTaskTable.order
}