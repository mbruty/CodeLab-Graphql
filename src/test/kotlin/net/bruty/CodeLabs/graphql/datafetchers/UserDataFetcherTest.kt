package net.bruty.CodeLabs.graphql.datafetchers

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import net.bruty.CodeLabs.graphql.CodeLabsGraphQLApplication
import net.bruty.CodeLabs.graphql.DbUtils
import net.bruty.CodeLabs.graphql.data.CookieHandler
import net.bruty.CodeLabs.graphql.data.CookieHandlerFactory
import net.bruty.CodeLabs.graphql.data.ICookieHandler
import net.bruty.CodeLabs.graphql.mocks.MockCookieHandlerSavedCookies
import net.bruty.CodeLabs.graphql.model.UserEntity
import net.bruty.CodeLabs.graphql.model.UsersTable
import net.bruty.CodeLabs.graphql.utils.AuthUtils
import net.bruty.CodeLabs.graphql.utils.TestDbUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.reflect.KClass

@SpringBootTest(classes = [
    DgsAutoConfiguration::class,
    CodeLabsGraphQLApplication::class
], properties= ["production=false"])
class UserDataFetcherTest {

    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    @BeforeEach
    fun setup() {
        AuthUtils.createTestUser()
        AuthUtils.loginUser(dgsQueryExecutor)
    }

    @AfterEach
    fun tearDown() {
        AuthUtils.deleteUsers()
        MockCookieHandlerSavedCookies.reset()
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupAll() {
            TestDbUtils.createTestDb()
            CookieHandlerFactory.factoryClass = MockCookieHandlerSavedCookies::class as KClass<ICookieHandler>
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            TestDbUtils.close()
            CookieHandlerFactory.factoryClass = CookieHandler::class as KClass<ICookieHandler>

        }
    }

    @Test
    fun authCheckThrowsWithNoLogin() {
        // Ensure all cookies are removed
        MockCookieHandlerSavedCookies.reset()

        val result = dgsQueryExecutor.execute("""
            {
                authCheck
            }
        """.trimIndent());

        assertEquals(1, result.errors.size);
        assertEquals(AuthUtils.UNAUTHORIZED_MESSAGE, result.errors.first().message);
    }

    @Test
    fun authCheck() {
        assertDoesNotThrow {
            val result = dgsQueryExecutor.executeAndExtractJsonPath<Boolean>("""
                {
                    authCheck
                }
            """.trimIndent(), "data.authCheck");

            assertTrue(result);
        }
    }

    @Test
    fun meThrowsWithNoLogin() {
        // Clear all cookies
        MockCookieHandlerSavedCookies.reset()
        val result = dgsQueryExecutor.execute("""
            {
                me {
                    id
                }
            }
        """.trimIndent())

        assertEquals(1, result.errors.size);
        assertEquals(AuthUtils.UNAUTHORIZED_MESSAGE, result.errors.first().message);
    }

    @Test
    fun me() {
        val result = dgsQueryExecutor.executeAndExtractJsonPath<String>("""
            {
                me {
                    id
                }
            }
        """.trimIndent(), "data.me.id")

        val expected = AuthUtils.getTestUser()
        assertEquals(expected.id.value.toString(), result);
    }

    @Test
    fun login() {
        MockCookieHandlerSavedCookies.reset()

        val result = dgsQueryExecutor.executeAndExtractJsonPath<String>("""
            mutation {
              login(email:"test@gmail.com", password:"test") {
                id
              }
            }
        """.trimIndent(), "data.login.id");

        val expected = AuthUtils.getTestUser()
        assertEquals(expected.id.value.toString(), result)
        assertEquals(2, MockCookieHandlerSavedCookies.cookies.size);
    }

    @Test
    fun loginWithBadPassword() {
        MockCookieHandlerSavedCookies.reset()

        val result = dgsQueryExecutor.execute("""
            mutation {
              login(email:"test@gmail.com", password:"test1") {
                id
              }
            }
        """.trimIndent());

        assertEquals(1, result.errors.size);
        assertEquals(AuthUtils.UNAUTHORIZED_MESSAGE, result.errors.first().message);
        assertEquals(0, MockCookieHandlerSavedCookies.cookies.size);
    }

    @Test
    fun logout() {
        assertEquals(2, MockCookieHandlerSavedCookies.cookies.size)
        val result = dgsQueryExecutor.execute("""
            mutation {
                logout
            }
        """.trimIndent());

        assertEquals(0, result.errors.size)
        assertEquals(0, MockCookieHandlerSavedCookies.cookies.size)
    }

    @Test
    fun logoutAll() {
        assertEquals(2, MockCookieHandlerSavedCookies.cookies.size)
        val userBefore = AuthUtils.getTestUser()
        val result = dgsQueryExecutor.execute("""
            mutation {
                logoutAll
            }
        """.trimIndent())

        val userAfter = AuthUtils.getTestUser()
        assertEquals(0, result.errors.size)
        assertEquals(0, MockCookieHandlerSavedCookies.cookies.size)
        assertEquals(userBefore.refreshCount + 1, userAfter.refreshCount)
    }

    @Test
    fun logoutAllThrowsErrorWithOldCookies() {
        val oldRefresh = MockCookieHandlerSavedCookies.cookies["refresh_token"]
        dgsQueryExecutor.execute("""
            mutation {
                logoutAll
            }
        """.trimIndent())

        // Use an empty string for the access token to bypass the 15-min token validity
        MockCookieHandlerSavedCookies.cookies["access_token"] = ""
        MockCookieHandlerSavedCookies.cookies["refresh_token"] = oldRefresh!!

        val result = dgsQueryExecutor.execute("""
            query {
                authCheck
            }
        """.trimIndent())

        assertTrue(result.errors.size > 0)
        assertEquals(AuthUtils.UNAUTHORIZED_MESSAGE, result.errors[0].message)
    }

    @Test
    fun signUp() {

        val result = dgsQueryExecutor.execute("""
            mutation {
                signUp(email: "test2@test.com", username: "test2", password: "test123!") {
                    id
                }
            }
        """.trimIndent())

        assertEquals(0, result.errors.size)

        val user = transaction {
            UserEntity.find { UsersTable.email eq "test2@test.com" }.firstOrNull()
        }

        assertNotNull(user)
        assertEquals("test2@test.com", user!!.email)
        assertEquals("test2", user.username)
    }
}