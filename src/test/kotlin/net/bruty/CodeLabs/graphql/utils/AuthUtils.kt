package net.bruty.CodeLabs.graphql.utils

import com.netflix.graphql.dgs.DgsQueryExecutor
import net.bruty.CodeLabs.graphql.model.UserEntity
import net.bruty.CodeLabs.graphql.model.UsersTable
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

class AuthUtils {
    companion object {
        val UNAUTHORIZED_MESSAGE = "net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException: Unauthorised and attempting to access protected data";
        fun createTestUser() {
            // Create the user
            transaction {
                UserEntity.new {
                    username = "test"
                    email = "test@gmail.com"
                    password = "test"
                }
            }
        }

        fun getTestUser(): UserEntity {
            return transaction {
                UserEntity.find { UsersTable.email eq "test@gmail.com" }.first()
            }
        }

        fun loginUser(dgsQueryExecutor: DgsQueryExecutor) {
            dgsQueryExecutor.execute("""
                mutation {
                  login(email:"test@gmail.com", password:"test") {
                    id
                  }
                }
            """.trimIndent())
        }

        fun deleteUsers() {
            transaction {
                UsersTable.deleteAll()
            }
        }
    }
}