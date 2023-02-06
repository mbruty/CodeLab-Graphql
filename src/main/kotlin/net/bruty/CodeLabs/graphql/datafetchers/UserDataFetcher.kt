package net.bruty.CodeLabs.graphql.datafetchers

import com.netflix.graphql.dgs.*
import net.bruty.CodeLabs.graphql.annotations.Authenticate
import net.bruty.CodeLabs.graphql.data.CookieHandlerFactory
import net.bruty.CodeLabs.graphql.exceptions.AlreadyExistsException
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserRepository
import net.bruty.CodeLabs.graphql.security.HttpContext
import net.bruty.CodeLabs.graphql.security.Security
import net.bruty.types.User
import org.springframework.beans.factory.annotation.Autowired

@DgsComponent
class UserDataFetcher {

    // region dependency-injected items
    @Autowired
    lateinit var _ctx: HttpContext

    @Autowired
    lateinit var _userRepo: IUserRepository

    @Autowired
    lateinit var _security: Security
    // endregion

    // region queries
    @Authenticate
    @DgsQuery
    fun authCheck(): Boolean {
        return true
    }

    @Authenticate
    @DgsQuery
    fun me(): User {
        // We can !! here as principal will always be set under @Authenticate
        var user = _userRepo.findByIdOrThrow(_ctx.principal!!.userId).toModel()
        user = user.copy(password = "REDACTED")
        return user
    }
    // endregion

    // region mutations
    @DgsMutation
    fun login(
        @InputArgument email: String,
        @InputArgument password: String,
        dfe: DgsDataFetchingEnvironment
    ): User {
        val user = _userRepo.findByEmail(email) ?: throw NotFoundException()
        if(!user.verifyHash(password)) throw UnauthorisedException()

        _security.setTokens(user.toPrincipal(), CookieHandlerFactory.getHandler(dfe))

        return user.toModel()
    }

    @Authenticate
    @DgsMutation
    fun logout(dfe: DgsDataFetchingEnvironment): Boolean {
        _security.logOut(CookieHandlerFactory.getHandler(dfe))
        return true
    }

    @Authenticate
    @DgsMutation
    fun logoutAll(dfe: DgsDataFetchingEnvironment): Boolean {
        _userRepo.logoutAll(_ctx.principal!!.userId)

        _security.logOut(CookieHandlerFactory.getHandler(dfe))
        return true
    }

    @DgsMutation
    fun signUp(
        @InputArgument email: String,
        @InputArgument username: String,
        @InputArgument password: String,
        dfe: DgsDataFetchingEnvironment
    ): User {
        val user = User(email = email, username = username, password = password, refreshCount = 0, xp = 0)
        try {
            val created = _userRepo.create(user)
            _security.setTokens(created.toPrincipal(), CookieHandlerFactory.getHandler(dfe))
            return created.toModel()

        } catch (e: Exception) {
            // This can only throw an error with inserting in to the SQL db
            throw AlreadyExistsException();
        }
    }
    // endregion

}