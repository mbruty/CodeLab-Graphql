package net.bruty.CodeLabs.graphql.model

import net.bruty.types.File
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object FileTable: IntIdTable() {
    val fileName: Column<String> = varchar("file_name", 25)
    val fileText: Column<String> = text("file_text")
    val task = reference("programming_task", ProgrammingTaskTable)
}
class FileEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<FileEntity>(FileTable)
    var fileName by FileTable.fileName
    var fileText by FileTable.fileText

    fun toDTO(): File {
        return File(
            this.fileName,
            this.fileText
        )
    }
}