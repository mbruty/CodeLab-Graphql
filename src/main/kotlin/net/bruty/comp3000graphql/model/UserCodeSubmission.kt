package net.bruty.comp3000graphql.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object UserCodeSubmissionTable: IntIdTable() {
    val codeText: Column<String> = text("code_text");
    val executionTime: Column<Int> = integer("execution_time");
    val memoryUsage: Column<String> = text("memory_usage");
    val isSubmitted: Column<Boolean> = bool("is_submitted").default(false);
    val hasSharedWithModuleStaff: Column<Boolean> = bool("has_shared_with_module_staff").default(false);
    val hasSharedWithStudents: Column<Boolean> = bool("has_shared_with_students").default(false);
    val createdBy = reference("created_by", UsersTable, ReferenceOption.CASCADE);
    val task = reference("task", ProgrammingTaskTable, ReferenceOption.CASCADE);
    val language = reference("language", LanguageTable, ReferenceOption.CASCADE);
}

class UserCodeSubmissionEntity(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<UserCodeSubmissionEntity>(UserCodeSubmissionTable) {
        const val SEPARATOR = ":"
    }
    var codeText by UserCodeSubmissionTable.codeText
    var executionTime by UserCodeSubmissionTable.executionTime
    var memoryUsage by UserCodeSubmissionTable.memoryUsage.transform(
        { a -> a.joinToString(SEPARATOR) },
        { str -> str.split(SEPARATOR).map { it.toIntOrNull() }.toTypedArray() }
    )
    var isSubmitted by UserCodeSubmissionTable.isSubmitted
    var hasSharedWithModuleStaff by UserCodeSubmissionTable.hasSharedWithModuleStaff
    var hasSharedWithStudents by UserCodeSubmissionTable.hasSharedWithStudents
    var createdBy by UserEntity referencedOn UserCodeSubmissionTable.createdBy
    var task by ProgrammingTaskEntity referencedOn UserCodeSubmissionTable.task
    var language by LanguageEntity referencedOn UserCodeSubmissionTable.language
}