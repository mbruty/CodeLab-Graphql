package net.bruty.CodeLabs.graphql.model

import net.bruty.CodeLabs.graphql.model.ProgrammingTaskEntity.Companion.referrersOn
import net.bruty.CodeLabs.graphql.security.IPrincipal
import net.bruty.CodeLabs.graphql.security.UserPrincipal
import net.bruty.types.User
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.mindrot.jbcrypt.BCrypt

object UsersTable: IntIdTable() {
    val email: Column<String> = varchar("email", 50).uniqueIndex()
    val username: Column<String> = varchar("name", 25)
    val password: Column<String> = varchar("password", 72) // 72 is Bcrypt maximum hash length
    val refereshCount: Column<Int> = integer("refresh_count").default(0)
    val xp: Column<Int> = integer("xp").default(0)
}
class UserEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UsersTable)
    var email by UsersTable.email
    var username by UsersTable.username
    var xp by UsersTable.xp
    val taskSubmissions by UserCodeSubmissionEntity referrersOn UserCodeSubmissionTable.createdBy

    private var _password by UsersTable.password

    var password: String
        get() {
            return _password
        }
        set(value) {
            _password = BCrypt.hashpw(value, BCrypt.gensalt(8))
        }

    var refreshCount by UsersTable.refereshCount

    fun toModel(): User {
        return User(id.value, email, username, password, refreshCount, xp)
    }

    fun verifyHash(password: String): Boolean {
        return BCrypt.checkpw(password, this._password)
    }

    fun toPrincipal(): IPrincipal {
        return UserPrincipal(this.id.value, this.refreshCount)
    }
}