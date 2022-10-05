package net.bruty.comp3000graphql.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object LanguageTable: IntIdTable() {
    val language: Column<String> = varchar("language", 50).uniqueIndex()
}
class LanguageEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<LanguageEntity>(LanguageTable)
    var language by LanguageTable.language
}