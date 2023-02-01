package net.bruty.CodeLabs.graphql.model

import net.bruty.CodeLabs.graphql.Cities.references
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object UserTimeLogTable: IntIdTable() {
    val task = reference("task", ProgrammingTaskTable, ReferenceOption.CASCADE)
    val user = reference("user", UsersTable, ReferenceOption.CASCADE)
    val date = date("log_date").clientDefault{ LocalDate.now() }
    val minutesLogged = integer("minutes_logged")
    init {
        // Add a unique index for:
        //
        index(true, user, task)
    }
}
class UserTimeLogEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserTimeLogEntity>(UserTimeLogTable)
    val task by UserTimeLogTable.task
    val user by UserTimeLogTable.user
    val date by UserTimeLogTable.date
    val minutesLogged by UserTimeLogTable.minutesLogged
}