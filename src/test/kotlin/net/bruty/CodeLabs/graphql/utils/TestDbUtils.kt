package net.bruty.CodeLabs.graphql.utils

import net.bruty.CodeLabs.graphql.DbUtils
import org.jetbrains.exposed.sql.Database
import org.testcontainers.containers.PostgreSQLContainer

class TestDbUtils {
    companion object {
        private var container: PostgreSQLContainer<*>? = null
        fun createTestDb() {
            container = PostgreSQLContainer("postgres:latest")
                .withDatabaseName("integration-tests")
                .withUsername("sa")
                .withPassword("sa")

            container?.start()

            Database.connect(
                container!!.jdbcUrl,
                driver = "org.postgresql.Driver",
                user = container!!.username,
                password = container!!.password
            )

            DbUtils.createTables()

        }
        fun close() {
            DbUtils.dropTables()
            container?.close()
        }
    }

}