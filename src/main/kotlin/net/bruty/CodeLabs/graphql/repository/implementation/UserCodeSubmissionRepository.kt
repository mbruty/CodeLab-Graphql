package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.model.*
import net.bruty.CodeLabs.graphql.model.ProgrammingTaskTable.entityId
import net.bruty.CodeLabs.graphql.repository.interfaces.IUserCodeSubmissionRepository
import net.bruty.CodeLabs.graphql.security.HttpContext
import net.bruty.types.UserCodeSubmission
import net.bruty.types.UserCodeSubmissionInput
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
            val userId = httpContext.principal!!.userId
            val pair = UserCodeSubmissionTable
                .innerJoin(UsersTable)
                .innerJoin(LanguageTable)
                .innerJoin(ProgrammingTaskStarterCodeTable)
                .innerJoin(ProgrammingTaskTable, { ProgrammingTaskTable.id }, { UserCodeSubmissionTable.task } )
                .slice(UserCodeSubmissionTable.id, LanguageTable.id)
                .select {
                    (UsersTable.id eq userId) and
                    (LanguageTable.language eq obj.language) and
                    (ProgrammingTaskTable.id eq obj.taskId)
                }.map { Pair(it[UserCodeSubmissionTable.id], it[LanguageTable.id]) }
                .firstOrNull()

            if(pair == null) {
                val foundLanguage = LanguageEntity.find { LanguageTable.language eq obj.language }
                    .firstOrNull() ?: throw NotFoundException()
                UserCodeSubmissionTable.insert {
                    it[task] = obj.taskId
                    it[codeText] = obj.codeText
                    it[executionTime] = obj.executionTime
                    it[memoryUsage] = obj.memoryUsage?.joinToString { UserCodeSubmissionEntity.SEPARATOR }
                    it[isSubmitted] = obj.isSubmitted ?: false
                    it[hasSharedWithModuleStaff] = obj.hasSharedWithModuleStaff ?: false
                    it[hasSharedWithStudents] = obj.hasSharedWithStudents ?: false
                    it[language] = foundLanguage.id
                    it[createdBy] = httpContext.principal?.userId ?: throw UnauthorisedException()
                }
            }
            else {
                UserCodeSubmissionTable.update({ UserCodeSubmissionTable.id eq pair.first }) {
                    it[codeText] = obj.codeText
                    it[executionTime] = obj.executionTime
                    it[memoryUsage] = obj.memoryUsage?.joinToString { UserCodeSubmissionEntity.SEPARATOR }
                    it[isSubmitted] = obj.isSubmitted ?: false
                    it[hasSharedWithModuleStaff] = obj.hasSharedWithModuleStaff ?: false
                    it[hasSharedWithStudents] = obj.hasSharedWithStudents ?: false
                    it[language] = pair.second
                    it[createdBy] = httpContext.principal?.userId ?: throw UnauthorisedException()
                }
            }
        }
    }
}