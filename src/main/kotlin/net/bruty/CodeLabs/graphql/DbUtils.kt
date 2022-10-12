package net.bruty.CodeLabs.graphql

import net.bruty.CodeLabs.graphql.model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
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
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(UsersTable)
                SchemaUtils.create(LanguageTable)
                SchemaUtils.create(ProgrammingTaskTable)
                SchemaUtils.create(UserCodeSubmissionTable)
                SchemaUtils.create(ProgrammingTaskStarterCodeTable)
            }
        }
    }
}