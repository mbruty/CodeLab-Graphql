package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.DbUtils
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.model.*
import net.bruty.CodeLabs.graphql.security.HttpContext
import net.bruty.CodeLabs.graphql.utils.TestDbUtils
import net.bruty.types.UserCodeSubmissionInput
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

internal class UserCodeSubmissionRepositoryTest {
    private lateinit var _repo: UserCodeSubmissionRepository
    private lateinit var _user: UserEntity
    private lateinit var _lang: LanguageEntity
    private lateinit var _task: ProgrammingTaskEntity
    private lateinit var _submission: UserCodeSubmissionEntity
    private lateinit var _httpCtx: HttpContext

    @BeforeEach
    fun setup() {
        _user = transaction {
            UserEntity.new {
                username = "test"
                email = "test@test.com"
                password = "testing123"
                xp = 0
            }
        }

        _lang = transaction {
            LanguageEntity.new {
                language = "test"
                queueIdentifier = "test"
            }
        }

        _task = transaction {
            ProgrammingTaskEntity.new {
                defaultLanguage = _lang
                title = "test"
                description = "test"
            }
        }

        _submission = transaction {
            UserCodeSubmissionEntity.new {
                codeText = "test"
                createdBy = _user
                task = _task
                language = _lang
            }
        }

        _httpCtx = HttpContext()
        _httpCtx.principal = _user.toPrincipal()
        _repo = UserCodeSubmissionRepository()
        _repo.httpContext = _httpCtx
    }

    @AfterEach
    fun tearDown() {
        transaction {
            UserCodeSubmissionTable.deleteAll()
            UsersTable.deleteAll()
            ProgrammingTaskTable.deleteAll()
            LanguageTable.deleteAll()
        }
    }

    @Test
    fun findByNonExistentId() {
        val found = _repo.findById(_submission.id.value + 1)
        assertNull(found);
    }

    @Test
    fun findByIdOrThrow() {
        assertDoesNotThrow {
            _repo.findByIdOrThrow(_submission.id.value);
        }
    }

    @Test
    fun findByIdOrThrowNonExistentId() {
        assertThrows<NotFoundException> {
            _repo.findByIdOrThrow(_submission.id.value + 1)
        }
    }

    @Test
    fun upsertWithNewItem() {
        val language2 = transaction {
            LanguageEntity.new {
                language = "test2"
                queueIdentifier = "TEST2"
            }
        }

        val args = UserCodeSubmissionInput(
            codeText = "test2",
            taskId = _task.id.value.toString(),
            language = language2.language
        )
        _repo.upsert(args)
        assertEquals(2, _repo.findAll().size)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupAll() {
            TestDbUtils.createTestDb();
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            TestDbUtils.close()

        }
    }
}