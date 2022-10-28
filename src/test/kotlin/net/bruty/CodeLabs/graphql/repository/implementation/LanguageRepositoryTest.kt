package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.DbUtils
import net.bruty.CodeLabs.graphql.model.LanguageTable
import net.bruty.CodeLabs.graphql.repository.implementation.LanguageRepository
import net.bruty.CodeLabs.graphql.repository.interfaces.ILanguageRepository
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll

internal class LanguageRepositoryTest {
    private lateinit var _repo: ILanguageRepository

    @BeforeEach
    fun setUp() {
        _repo = LanguageRepository()
    }

    @AfterEach
    fun tearDown() {
        transaction {
            LanguageTable.deleteAll()
        }
    }

    @Test
    fun getQueueNameByLanguage() {
        transaction {
            LanguageTable.insert {
                it[language] = "test"
                it[queueIdentifier] = "test_queue"
            }
        }

        val queue = _repo.getQueueNameByLanguage("test")
        assertEquals("test_queue", queue);
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupAll() {
            DbUtils.connectTest();
            DbUtils.createTables()
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            DbUtils.dropTables()
        }
    }
}