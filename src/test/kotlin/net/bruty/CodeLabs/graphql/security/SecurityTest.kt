package net.bruty.CodeLabs.graphql.security

import net.bruty.CodeLabs.graphql.DbUtils
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.mocks.MockCookieHandler
import net.bruty.CodeLabs.graphql.mocks.MockSecurity
import net.bruty.CodeLabs.graphql.model.UserEntity
import net.bruty.CodeLabs.graphql.model.UsersTable
import net.bruty.CodeLabs.graphql.repository.implementation.UserRepository
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows

internal class SecurityTest {
    private lateinit var _cookieHandler: MockCookieHandler
    private lateinit var _mockSecurity: MockSecurity // Only used to override the expiration time
    private lateinit var _security: Security
    private lateinit var _user: UserEntity

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

    @BeforeEach
    fun setup() {
        _cookieHandler = MockCookieHandler()
        _mockSecurity = MockSecurity()
        _security = Security()
        val userRepo = UserRepository()
        _mockSecurity._userRepository = userRepo
        _security._userRepository = userRepo

        // Create the user
        _user = transaction {
            UserEntity.new {
                username = "test"
                email = "test@gmail.com"
                password = "test"

            }
        }
    }

    @AfterEach
    fun tearDown() {
        transaction {
            UsersTable.deleteAll()
        }
    }

    @Test
    fun `setTokens sets tokens`() {
        // Cookies should be null
        assertNull(_cookieHandler.cookies["access_token"])
        assertNull(_cookieHandler.cookies["refresh_token"])
        _mockSecurity.setTokens(_user.toPrincipal(), _cookieHandler);

        assertNotNull(_cookieHandler.cookies["access_token"])
        assertNotNull(_cookieHandler.cookies["refresh_token"])
    }

    @Test
    fun `processToken returns correct principal for access`() {
        _security.setTokens(_user.toPrincipal(), _cookieHandler)
        val refresh = _cookieHandler.cookies["access_token"] ?: fail("Access token is null")
        val user = _security.processToken(refresh) ?: fail("Token could not be parsed")
        assertEquals(_user.id.value.toString(), user.userId)
        assertEquals(_user.refreshCount, user.refreshCount)
    }

    @Test
    fun `processToken returns correct principal for refresh`() {
        _security.setTokens(_user.toPrincipal(), _cookieHandler)
        val refresh = _cookieHandler.cookies["refresh_token"] ?: fail("Refresh token is null")
        val user = _security.processToken(refresh) ?: fail("Token could not be parsed")
        assertEquals(_user.id.value.toString(), user.userId)
        assertEquals(_user.refreshCount, user.refreshCount)
    }

    @Test
    fun `processToken returns null for an expired JWT`() {
        _mockSecurity.accessExpirationSeconds = -1 // Already expired token
        _mockSecurity.refreshExpirationSeconds = -1
        _mockSecurity.setTokens(_user.toPrincipal(), _cookieHandler)
        val refresh = _cookieHandler.cookies["refresh_token"] ?: fail("Refresh token is null")
        assertNull(_security.processToken(refresh))
    }

    @Test
    fun `getPrincipal returns correct principal with valid access token`() {
        _mockSecurity.accessExpirationSeconds = 100
        _mockSecurity.refreshExpirationSeconds = 100
        _mockSecurity.setTokens(_user.toPrincipal(), _cookieHandler)

        val access = _cookieHandler.cookies["access_token"] ?: fail("Access token is null")
        val refresh = _cookieHandler.cookies["refresh_token"] ?: fail("Refresh token is null")
        val user = _security.getPrincipal(access, refresh, _cookieHandler)

        assertEquals(_user.id.value.toString(), user.userId)
        assertEquals(_user.refreshCount, user.refreshCount)
    }

    @Test
    fun `getPrincipal returns correct principal and refreshes access_token with expired access but valid refresh`() {
        _mockSecurity.accessExpirationSeconds = -1
        _mockSecurity.refreshExpirationSeconds = 100
        _mockSecurity.setTokens(_user.toPrincipal(), _cookieHandler)

        val access = _cookieHandler.cookies["access_token"] ?: fail("Access token is null")
        val refresh = _cookieHandler.cookies["refresh_token"] ?: fail("Refresh token is null")
        val user = _security.getPrincipal(access, refresh, _cookieHandler)

        assertEquals(_user.id.value.toString(), user.userId)
        assertEquals(_user.refreshCount, user.refreshCount)

        val accessRefreshed = _cookieHandler.cookies["access_token"] ?: fail("Access token is null")
        assertNotEquals(access, accessRefreshed)

    }

    @Test
    fun `getPrincipal throws with expired access and refresh`() {
        _mockSecurity.accessExpirationSeconds = -1
        _mockSecurity.refreshExpirationSeconds = -1
        _mockSecurity.setTokens(_user.toPrincipal(), _cookieHandler)
        val access = _cookieHandler.cookies["access_token"] ?: fail("Access token is null")
        val refresh = _cookieHandler.cookies["refresh_token"] ?: fail("Refresh token is null")
        assertThrows<UnauthorisedException> {
            _mockSecurity.getPrincipal(access, refresh, _cookieHandler)
        }
    }

    @Test
    fun `getPrincipal throws with expired access and valid refresh, but refresh count has been incremented`() {
        _mockSecurity.accessExpirationSeconds = -1
        _mockSecurity.setTokens(_user.toPrincipal(), _cookieHandler)
        transaction {
            _user.refreshCount++
        }

        val access = _cookieHandler.cookies["access_token"] ?: fail("Access token is null")
        val refresh = _cookieHandler.cookies["refresh_token"] ?: fail("Refresh token is null")
        assertThrows<UnauthorisedException> {
            _security.getPrincipal(access, refresh, _cookieHandler)
        }
    }
}