package net.bruty.CodeLabs.graphql.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object UserModuleTable: IntIdTable() {
    val module = reference("module", ModuleTable, ReferenceOption.CASCADE)
    val user = reference("user", UsersTable, ReferenceOption.CASCADE)

    init {
        // Add a unique index for:
        // A user can only enrol once on to a module
        index(true, module, user)
    }
}
class UserModuleEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserModuleEntity>(UserModuleTable)
    val module by UserModuleTable.module
    val user by UserModuleTable.user
}