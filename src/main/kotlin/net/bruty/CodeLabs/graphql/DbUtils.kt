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
            Database.connect("jdbc:postgresql://pgsql.bruty.net/CodeLabs",
                driver = "org.postgresql.Driver",
                user = "postgres",
                password = "fDpw238ECFRmPnadqHmK1lEP9",
            )
        }

        fun connectTest() {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver", user = "root", password = "")
        }

        fun createTables() {
            transaction {
                SchemaUtils.create(UsersTable)
                SchemaUtils.create(LanguageTable)
                SchemaUtils.create(ProgrammingTaskTable)
                SchemaUtils.create(UserCodeSubmissionTable)
                SchemaUtils.create(ProgrammingTaskStarterCodeTable)
            }
        }

        fun dropTables() {
            transaction {
                SchemaUtils.drop(UserCodeSubmissionTable)
                SchemaUtils.drop(ProgrammingTaskStarterCodeTable)
                SchemaUtils.drop(ProgrammingTaskTable)
                SchemaUtils.drop(LanguageTable)
                SchemaUtils.drop(UsersTable)
            }
        }
    }
}