package net.bruty.CodeLabs.graphql.model

import net.bruty.CodeLabs.graphql.model.ModuleTaskTable.references
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object ModuleTable: IntIdTable() {
    val moduleTitle: Column<String> = varchar("module_title", 128)
    val moduleDescription: Column<String> = text("module_description")
    val createdBy = reference("created_by", UsersTable)
}
class ModuleEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ModuleEntity>(ModuleTable)
    var title by ModuleTable.moduleTitle
    var description by ModuleTable.moduleDescription
    var tasks by ProgrammingTaskEntity via ModuleTaskTable
    var createdBy by UserEntity referencedOn ModuleTable.createdBy
}