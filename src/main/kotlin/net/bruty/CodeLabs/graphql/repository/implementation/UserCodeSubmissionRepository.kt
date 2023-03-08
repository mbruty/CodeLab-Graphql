package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.extensions.toUUID
import net.bruty.CodeLabs.graphql.model.*
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserCodeSubmissionRepository
import net.bruty.CodeLabs.graphql.security.HttpContext
import net.bruty.types.UserCodeSubmission
import net.bruty.types.UserCodeSubmissionInput
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.NullPointerException

@Component
class UserCodeSubmissionRepository: IUserCodeSubmissionRepository {

    @Autowired
    lateinit var httpContext: HttpContext

    override fun findById(id: Int): UserCodeSubmissionEntity? {
        return transaction {
            UserCodeSubmissionEntity.findById(id)
        }
    }

    override fun findByIdOrThrow(id: Int): UserCodeSubmissionEntity {
        return transaction {
            UserCodeSubmissionEntity.findById(id) ?: throw NotFoundException()
        }
    }

    override fun findAll(): List<UserCodeSubmissionEntity> {
        return transaction {
            UserCodeSubmissionEntity.all().toList()
        }
    }

    override fun create(obj: UserCodeSubmission): UserCodeSubmissionEntity {
        throw Exception("Use upsert instead")
    }

    override fun update(obj: UserCodeSubmission): UserCodeSubmissionEntity {
        throw Exception("Use upsert instead")
    }

    override fun upsert(obj: UserCodeSubmissionInput) {
        return transaction {
            addLogger(StdOutSqlLogger)
            val userId = httpContext.principal?.userUUID ?: throw UnauthorisedException()
            val pair = UserCodeSubmissionTable
                .innerJoin(UsersTable)
                .innerJoin(LanguageTable)
                .innerJoin(ProgrammingTaskStarterCodeTable)
                .innerJoin(ProgrammingTaskTable, { ProgrammingTaskTable.id }, { UserCodeSubmissionTable.task } )
                .slice(UserCodeSubmissionTable.id, LanguageTable.id)
                .select {
                    (UsersTable.id eq userId) and
                    (LanguageTable.language eq obj.language) and
                    (ProgrammingTaskTable.id eq obj.taskId.toUUID())
                }.map { Pair(it[UserCodeSubmissionTable.id], it[LanguageTable.id]) }
                .firstOrNull()

            val foundLanguage = LanguageEntity.find { LanguageTable.language eq obj.language }
                .firstOrNull() ?: throw NotFoundException()
            val foundTask = ProgrammingTaskEntity.findById(obj.taskId.toUUID()) ?: throw NotFoundException()
            val user = UserEntity.findById(userId) ?: throw UnauthorisedException()

            if(pair == null) {
                UserCodeSubmissionEntity.new {
                    task = foundTask
                    codeText = obj.codeText
                    executionTime = obj.executionTime
                    memoryUsage = obj.memoryUsage?.toTypedArray() ?: emptyArray()
                    isSubmitted = obj.isSubmitted ?: false
                    hasSharedWithModuleStaff = obj.hasSharedWithModuleStaff ?: false
                    language = foundLanguage
                    createdBy = user
                }
            }
            else {
                val existing = UserCodeSubmissionEntity.findById(pair.first) ?: throw NotFoundException()
                existing.codeText = obj.codeText
                existing.executionTime = obj.executionTime
                existing.memoryUsage = obj.memoryUsage?.toTypedArray() ?: emptyArray()
                existing.isSubmitted = obj.isSubmitted ?: false
                existing.hasSharedWithModuleStaff = obj.hasSharedWithModuleStaff ?: false
                existing.hasSharedWithStudents = obj.hasSharedWithStudents ?: false
                existing.language = foundLanguage
                existing.createdBy = user
            }
        }
    }
}