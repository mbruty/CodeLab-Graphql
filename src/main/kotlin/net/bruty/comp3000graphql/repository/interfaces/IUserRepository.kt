package net.bruty.comp3000graphql.repository.interfaces

import net.bruty.comp3000graphql.model.UserEntity
import net.bruty.types.User

interface IUserRepository: IIntIDRepository<UserEntity, User> {
    fun findByEmail(email: String): UserEntity?
    fun logoutAll(user: UserEntity)
}