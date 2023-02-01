package net.bruty.CodeLabs.graphql.model

import net.bruty.types.ProgrammingTask
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object ProgrammingTaskTable: IntIdTable() {
    val title: Column<String> = varchar("title", 100)
    val description: Column<String> = text("description")
    val defaultLanguage = reference("default_language", LanguageTable);
}
class ProgrammingTaskEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ProgrammingTaskEntity>(ProgrammingTaskTable)
    var title by ProgrammingTaskTable.title
    var description by ProgrammingTaskTable.description
    val startCodes by ProgrammingTaskStarterCodeEntity referrersOn ProgrammingTaskStarterCodeTable.task
    val userSubmissions by UserCodeSubmissionEntity referrersOn UserCodeSubmissionTable.task
    var defaultLanguage by LanguageEntity referencedOn ProgrammingTaskTable.defaultLanguage
}