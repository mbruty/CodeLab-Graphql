package net.bruty.comp3000graphql.repository.implementation

import net.bruty.comp3000graphql.exceptions.NotFoundException
import net.bruty.comp3000graphql.model.UserEntity
import net.bruty.comp3000graphql.model.UsersTable
import net.bruty.comp3000graphql.repository.interfaces.IUserRepository
import net.bruty.types.User
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component

@Component
class UserRepository: IUserRepository {
    override fun findByEmail(email: String): UserEntity? {
        return transaction {
            UserEntity.find { UsersTable.email eq email }.firstOrNull()
        }
    }

    override fun logoutAll(user: UserEntity) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun create(obj: User): UserEntity {
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