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

        fun createTables() {
            transaction {
                SchemaUtils.create(UsersTable)
                SchemaUtils.create(ModuleTable)
                SchemaUtils.create(LanguageTable)
                SchemaUtils.create(ModuleTaskTable)
                SchemaUtils.create(UserModuleTable)
                SchemaUtils.create(UserTimeLogTable)
                SchemaUtils.create(ProgrammingTaskTable)
                SchemaUtils.create(FileTable)
                SchemaUtils.create(UserCodeSubmissionTable)
                SchemaUtils.create(ProgrammingTaskStarterCodeTable)
            }
        }

        fun dropTables() {
            transaction {
                SchemaUtils.drop(UserCodeSubmissionTable)
                SchemaUtils.drop(ProgrammingTaskStarterCodeTable)
                SchemaUtils.drop(ModuleTaskTable)
                SchemaUtils.drop(UserTimeLogTable)
                SchemaUtils.drop(FileTable)
                SchemaUtils.drop(ProgrammingTaskTable)
                SchemaUtils.drop(UserModuleTable)
                SchemaUtils.drop(ModuleTaskTable)
                SchemaUtils.drop(LanguageTable)
                SchemaUtils.drop(ModuleTable)
                SchemaUtils.drop(UsersTable)
            }
        }
    }
}