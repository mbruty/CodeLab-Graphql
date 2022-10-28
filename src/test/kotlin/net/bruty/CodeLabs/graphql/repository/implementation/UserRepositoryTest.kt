package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.DbUtils
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.model.UserEntity
import net.bruty.CodeLabs.graphql.model.UsersTable
import net.bruty.types.User
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*

internal class UserRepositoryTest {
    private val _repo = UserRepository()
    private var currentId = -1;
    @BeforeEach
    fun setUp() {
        transaction {
            val created = UserEntity.new {
                email = "test@test.com"
                password = "secure_password"
                username = "test"
            }
            currentId = created.id.value
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
        assertEquals(currentId, found!!.id.value)
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
        assertThrows<NotFoundException> { _repo.findByIdOrThrow(-1) }
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
        val user = User(username = "test-2", email = "test-2@test.com", password = "pass", refreshCount = 0, xp = 0);
        val created = _repo.create(user);
        assertEquals("test-2", created.username)
        assertEquals("test-2@test.com", created.email)

        val allUsers = _repo.findAll()
        val mostRecent = allUsers.maxByOrNull { it.id.value }!!
        assertEquals("test-2", mostRecent.username)
        assertEquals("test-2@test.com", mostRecent.email)

        val found = _repo.findByIdOrThrow(mostRecent.id.value)
        assertEquals("test-2", found.username)
        assertEquals("test-2@test.com", found.email)
    }

    @Test
    fun createWithBadEmail() {
        assertThrows<Exception> { _repo.create(User(username = "", email = "test", password = "", refreshCount = 0, xp = 0)) }
    }
    @Test
    fun update() {
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