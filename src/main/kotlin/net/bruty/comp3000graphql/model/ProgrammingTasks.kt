package net.bruty.comp3000graphql.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object ProgrammingTaskTable: IntIdTable() {
    val title: Column<String> = varchar("title", 100)
    val description: Column<String> = text("description")
}
class ProgrammingTaskEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ProgrammingTaskEntity>(ProgrammingTaskTable)
    var title by ProgrammingTaskTable.title
    var description by ProgrammingTaskTable.description
    val startCodes by ProgrammingTaskStarterCodeEntity referrersOn ProgrammingTaskStarterCodeTable.task
}