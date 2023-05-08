package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.DbUtils
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.model.UserEntity
import net.bruty.CodeLabs.graphql.model.UsersTable
import net.bruty.CodeLabs.graphql.utils.TestDbUtils
import net.bruty.types.User
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.util.UUID
import org.junit.jupiter.api.Assertions.*

internal class UserRepositoryTest {
    private val _repo = UserRepository()
    private var currentId = "";
    @BeforeEach
    fun setUp() {
        currentId = transaction {
            val created = UserEntity.new {
                email = "test@test.com"
                password = "secure_password"
                username = "test"
            }
            created.id.value.toString()
        }
    }

    @AfterEach
    fun tearDown() {
        transaction {
            UsersTable.deleteAll()
        }
    }

    @Test
    fun findByEmail() {
        val found = _repo.findByEmail("test@test.com");
        assertNotNull(found);
        assertEquals(currentId, found!!.id.value.toString())
    }

    @Test
    fun findByEmailWithBadEmailIsNull() {
        val found = _repo.findByEmail("");
        assertNull(found)
    }

    @Test
    fun findById() {
        val found = _repo.findById(currentId);
        assertNotNull(found);
        assertEquals("test@test.com", found!!.email)
        assertEquals("test", found.username)
    }

    @Test
    fun logoutAll() {
        val user1 = _repo.findById(currentId);
        assertNotNull(user1);
        _repo.logoutAll(currentId)
        val user2 = _repo.findById(currentId)!!;
        assertNotEquals(user1!!.refreshCount, user2.refreshCount);
        assertEquals(1, user2.refreshCount)
    }

    @Test
    fun findByIdOrThrow() {
        assertThrows<NotFoundException> { _repo.findByIdOrThrow(UUID.randomUUID().toString()) }
        assertDoesNotThrow { _repo.findByIdOrThrow(currentId) }
    }

    @Test
    fun findAll() {
        val toBeFound = mutableListOf<String>();
        for (i in 0..10) {
            transaction {
                UserEntity.new {
                    email = "test-$i@test.com"
                    password = "secure_password"
                    username = "test"
                }
                toBeFound.add("test-$i@test.com")
            }
        }

        val allUsers = _repo.findAll();

        allUsers.forEach { user ->
            toBeFound.remove(user.email)
        }

        assertEquals(0, toBeFound.size)
    }

    @Test
    fun create() {
        val user = User(username = "test-2", email = "test-2@test.com", password = "pass", refreshCount = 0, xp = 0, id = "");
        val created = _repo.create(user);
        assertEquals("test-2", created.username)
        assertEquals("test-2@test.com", created.email)
    }

    @Test
    fun createWithBadEmail() {
        assertThrows<Exception> { _repo.create(User(username = "", email = "test", password = "", refreshCount = 0, xp = 0, id = "")) }
    }
    @Test
    fun update() {
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupAll() {
            TestDbUtils.createTestDb()
            DbUtils.createTables()
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            TestDbUtils.close()

        }
    }
}