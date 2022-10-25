package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.model.UserEntity
import net.bruty.CodeLabs.graphql.model.UsersTable
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserRepository
import net.bruty.types.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException.BadRequest

@Component
class UserRepository: IUserRepository {
    override fun findByEmail(email: String): UserEntity? {
        return transaction {
            UserEntity.find { UsersTable.email eq email }.firstOrNull()
        }
    }

    override fun logoutAll(id: Int) {
        transaction {
            UsersTable.update({ UsersTable.id eq id }) {
                it[refereshCount] = refereshCount + 1
            }
        }
    }

    override fun findById(id: Int): UserEntity? {
        return transaction {
            UserEntity.findById(id)
        }
    }

    override fun findByIdOrThrow(id: Int): UserEntity {
        return transaction {
            UserEntity.findById(id) ?: throw NotFoundException()
        }
    }

    override fun findAll(): List<UserEntity> {
        return transaction {
            UserEntity.all().toList()
        }
    }

    override fun create(obj: User): UserEntity {
        val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})";
        if(!EMAIL_REGEX.toRegex().matches(obj.email)) {
            throw Exception("Invalid email");
        }
        return transaction {
            UserEntity.new {
                email = obj.email
                password = obj.password
                username = obj.username
            }
        }
    }

    override fun update(obj: User): UserEntity {
        TODO("Not yet implemented")
    }
}