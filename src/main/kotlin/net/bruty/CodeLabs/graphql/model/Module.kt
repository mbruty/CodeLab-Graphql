package net.bruty.CodeLabs.graphql.model

import net.bruty.types.Module
import java.util.UUID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column

object ModuleTable: UUIDTable() {
    val moduleTitle: Column<String> = varchar("module_title", 128)
    val moduleDescription: Column<String> = text("module_description")
    val createdBy = reference("created_by", UsersTable)
}
class ModuleEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<ModuleEntity>(ModuleTable)
    var title by ModuleTable.moduleTitle
    var description by ModuleTable.moduleDescription
    var tasks by ProgrammingTaskEntity via ModuleTaskTable
    var createdBy by UserEntity referencedOn ModuleTable.createdBy

    fun toDTO(): Module {
        return Module(
            id = this.id.value.toString(),
            title = this.title,
            description = this.description,
            tasks = this.tasks.map { it.toDTO() },
            createdBy = this.createdBy.toModel(),
            completedPct = -1.0
        )
    }
}