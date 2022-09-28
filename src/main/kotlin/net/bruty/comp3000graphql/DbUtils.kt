package net.bruty.comp3000graphql

import net.bruty.comp3000graphql.model.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DbUtils {
    companion object {
        fun connect() {
            Database.connect("jdbc:postgresql://51.195.149.191:5432/fyp",
                driver = "org.postgresql.Driver",
                user = "postgres",
                password = "fDpw238ECFRmPnadqHmK1lEP9",
            )
        }

        fun createTables() {
            transaction {
                SchemaUtils.create(UsersTable)
            }
        }
    }
}