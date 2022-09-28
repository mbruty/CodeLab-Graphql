package net.bruty.comp3000graphql.datafetchers

import com.netflix.graphql.dgs.*
import net.bruty.comp3000graphql.annotations.Authenticate
import net.bruty.comp3000graphql.data.CookieHandlerFactory
import net.bruty.comp3000graphql.exceptions.NotFoundException
import net.bruty.comp3000graphql.exceptions.UnauthorisedException
import net.bruty.comp3000graphql.repository.interfaces.IUserRepository
import net.bruty.comp3000graphql.security.HttpContext
import net.bruty.comp3000graphql.security.Security
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
        val user = _userRepo.findByIdOrThrow(_ctx.principal!!.userId)

        _userRepo.logoutAll(user)

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
        val user = User(email = email, username = username, password = password, refreshCount = 0)
        val created = _userRepo.create(user)

        _security.setTokens(created.toPrincipal(), CookieHandlerFactory.getHandler(dfe))

        return created.toModel()
    }
    // endregion

}