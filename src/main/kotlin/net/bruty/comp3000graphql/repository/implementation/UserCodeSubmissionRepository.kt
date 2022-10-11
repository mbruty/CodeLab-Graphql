package net.bruty.comp3000graphql.repository.implementation

import net.bruty.comp3000graphql.exceptions.NotFoundException
import net.bruty.comp3000graphql.model.*
import net.bruty.comp3000graphql.repository.interfaces.IUserCodeSubmissionRepository
import net.bruty.comp3000graphql.security.HttpContext
import net.bruty.types.ProgrammingTask
import net.bruty.types.UserCodeSubmission
import net.bruty.types.UserCodeSubmissionInput
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserCodeSubmissionRepository: IUserCodeSubmissionRepository {

    @Autowired
    lateinit var httpContext: HttpContext

    override fun findById(id: Int): UserCodeSubmissionEntity? {
        TODO("Not yet implemented")
    }

    override fun findByIdOrThrow(id: Int): UserCodeSubmissionEntity {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<UserCodeSubmissionEntity> {
        TODO("Not yet implemented")
    }

    override fun create(obj: UserCodeSubmission): UserCodeSubmissionEntity {
        throw Exception("Use upsert instead")
    }

    override fun update(obj: UserCodeSubmission): UserCodeSubmissionEntity {
        TODO("Not yet implemented")
    }

    override fun upsert(obj: UserCodeSubmissionInput) {
        return transaction {
            val foundUser = UserEntity.findById(httpContext.principal!!.userId) ?: throw NotFoundException()
            val foundTask = ProgrammingTaskEntity.findById(obj.taskId) ?: throw NotFoundException()
            val foundLanguage = LanguageEntity.find { LanguageTable.language eq obj.language }.firstOrNull() ?: throw NotFoundException()
            UserCodeSubmissionEntity.new {
                codeText = obj.codeText
                executionTime =  obj.executionTime ?: 0
                memoryUsage = obj.memoryUsage?.toTypedArray() ?: emptyArray()
                isSubmitted = obj.isSubmitted ?: false
                hasSharedWithModuleStaff = obj.hasSharedWithModuleStaff ?: false
                hasSharedWithStudents = obj.hasSharedWithStudents ?: false
                task = foundTask
                language = foundLanguage
                createdBy = foundUser
            }
        }
    }
}