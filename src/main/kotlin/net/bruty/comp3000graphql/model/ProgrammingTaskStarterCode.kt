package net.bruty.comp3000graphql.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object ProgrammingTaskStarterCodeTable: IntIdTable() {
    val unitTestCode: Column<String> = text("unit_test_code")
    val starterCode: Column<String> = text("starter_code")
    val language = reference("language", LanguageTable)
    val task = reference("task", ProgrammingTaskTable, ReferenceOption.CASCADE)
}
class ProgrammingTaskStarterCodeEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ProgrammingTaskStarterCodeEntity>(ProgrammingTaskStarterCodeTable)
    var unitTestCode by ProgrammingTaskStarterCodeTable.unitTestCode
    var starterCode by ProgrammingTaskStarterCodeTable.starterCode
    var language by LanguageEntity referencedOn ProgrammingTaskStarterCodeTable.language
    var task by ProgrammingTaskEntity referencedOn ProgrammingTaskStarterCodeTable.task
}