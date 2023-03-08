package net.bruty.CodeLabs.graphql.model

import net.bruty.types.ProgrammingTask
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import java.util.UUID

object ProgrammingTaskTable: UUIDTable() {
    val title: Column<String> = varchar("title", 100)
    val description: Column<String> = text("description")
    val defaultLanguage = reference("default_language", LanguageTable);
}
class ProgrammingTaskEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<ProgrammingTaskEntity>(ProgrammingTaskTable)
    var title by ProgrammingTaskTable.title
    var description by ProgrammingTaskTable.description
    val startCodes by ProgrammingTaskStarterCodeEntity referrersOn ProgrammingTaskStarterCodeTable.task
    val userSubmissions by UserCodeSubmissionEntity referrersOn UserCodeSubmissionTable.task
    val files by FileEntity referrersOn FileTable.task
    var defaultLanguage by LanguageEntity referencedOn ProgrammingTaskTable.defaultLanguage

    fun toDTO(): ProgrammingTask {
        return ProgrammingTask (
            id = this.id.value.toString(),
            title = this.title,
            description = this.description,
        );
    }
}