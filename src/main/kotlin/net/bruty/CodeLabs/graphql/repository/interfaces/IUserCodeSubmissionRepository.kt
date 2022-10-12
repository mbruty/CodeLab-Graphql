package net.bruty.CodeLabs.graphql.repository.interfaces

import net.bruty.CodeLabs.graphql.model.ProgrammingTaskEntity
import net.bruty.CodeLabs.graphql.model.UserCodeSubmissionEntity
import net.bruty.types.ProgrammingTask
import net.bruty.types.UserCodeSubmission
import net.bruty.types.UserCodeSubmissionInput

interface IUserCodeSubmissionRepository: IIntIDRepository<UserCodeSubmissionEntity, UserCodeSubmission> {
    fun upsert(obj: UserCodeSubmissionInput)
}