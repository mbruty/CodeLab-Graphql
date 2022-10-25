package net.bruty.CodeLabs.graphql.repository.interfaces

import net.bruty.CodeLabs.graphql.model.UserEntity
import net.bruty.types.User

interface IUserRepository: IIntIDRepository<UserEntity, User> {
    fun findByEmail(email: String): UserEntity?
    fun logoutAll(id: Int)
}