package net.bruty.comp3000graphql.repository.interfaces

import net.bruty.comp3000graphql.model.ProgrammingTaskEntity
import net.bruty.comp3000graphql.model.UserCodeSubmissionEntity
import net.bruty.types.ProgrammingTask
import net.bruty.types.UserCodeSubmission
import net.bruty.types.UserCodeSubmissionInput

interface IUserCodeSubmissionRepository: IIntIDRepository<UserCodeSubmissionEntity, UserCodeSubmission> {
    fun upsert(obj: UserCodeSubmissionInput)
}