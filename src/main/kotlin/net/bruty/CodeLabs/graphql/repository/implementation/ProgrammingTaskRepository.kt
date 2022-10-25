package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.model.*
import net.bruty.CodeLabs.graphql.repository.interfaces.IProgrammingTaskRepository
import net.bruty.CodeLabs.graphql.security.HttpContext
import net.bruty.types.ProgrammingTask
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProgrammingTaskRepository: IProgrammingTaskRepository {
    @Autowired
    lateinit var httpCtx: HttpContext;
    override fun getStarterCodeByLanguage(id: Int, language: String): ProgrammingTask {
        return transaction {
            addLogger(StdOutSqlLogger)
            val codeSubmission = UserCodeSubmissionTable
                .select {
                UserCodeSubmissionTable.createdBy eq (httpCtx.principal!!.userId)
            }
                .alias("ucs")
            val x = ProgrammingTaskTable
                .innerJoin(ProgrammingTaskStarterCodeTable)
                .innerJoin(LanguageTable, { LanguageTable.id }, { ProgrammingTaskStarterCodeTable.language })
                .leftJoin(codeSubmission, { codeSubmission[UserCodeSubmissionTable.task] }, { ProgrammingTaskStarterCodeTable.id })
                .leftJoin(UsersTable, { codeSubmission[UserCodeSubmissionTable.createdBy] }, { UsersTable.id })
                .select {
                    ProgrammingTaskTable.id eq id and (LanguageTable.language eq language)
                }.singleOrNull() ?: throw NotFoundException()

            var myCode = x[codeSubmission[UserCodeSubmissionTable.codeText]]

            if(myCode == null) {
                myCode = x[ProgrammingTaskStarterCodeTable.starterCode]
            }

            var task = ProgrammingTask(
                id = x[ProgrammingTaskTable.id].value.toString() + "." + x[LanguageTable.id],
                title = x[ProgrammingTaskTable.title],
                description = x[ProgrammingTaskTable.description],
                starterCode = x[ProgrammingTaskStarterCodeTable.starterCode],
                testCode = x[ProgrammingTaskStarterCodeTable.unitTestCode],
                language = x[LanguageTable.language],
                myCode = myCode,
                availableLanguages = emptyList<String>() // This will be set by a sub-resolver if included
            )

            if(task.myCode == "") {
                task = task.copy(myCode = task.starterCode)
            }
            return@transaction task;
        }
    }

    override fun getStarterCodeDefault(id: Int): ProgrammingTask {
        return transaction {
            addLogger(StdOutSqlLogger)
            val codeSubmission = UserCodeSubmissionTable
                .select {
                    UserCodeSubmissionTable.createdBy eq (httpCtx.principal!!.userId)
                }
                .alias("ucs")

            val x = ProgrammingTaskTable
                .innerJoin(ProgrammingTaskStarterCodeTable)
                .innerJoin(LanguageTable, { LanguageTable.id }, { ProgrammingTaskStarterCodeTable.language })
                .leftJoin(codeSubmission, { codeSubmission[UserCodeSubmissionTable.task] }, { ProgrammingTaskStarterCodeTable.id })
                .leftJoin(UsersTable, { codeSubmission[UserCodeSubmissionTable.createdBy] }, { UsersTable.id })
                .select {
                    (ProgrammingTaskTable.id eq id) and
                    (ProgrammingTaskTable.defaultLanguage eq ProgrammingTaskStarterCodeTable.language)
                }
                .singleOrNull() ?: throw NotFoundException()
            var myCode = x[codeSubmission[UserCodeSubmissionTable.codeText]]

            if(myCode == null) {
                myCode = x[ProgrammingTaskStarterCodeTable.starterCode]
            }
            var task = ProgrammingTask(
                id = x[ProgrammingTaskTable.id].value.toString() + "." + x[LanguageTable.id],
                title = x[ProgrammingTaskTable.title],
                description = x[ProgrammingTaskTable.description],
                starterCode = x[ProgrammingTaskStarterCodeTable.starterCode],
                testCode = x[ProgrammingTaskStarterCodeTable.unitTestCode],
                language = x[LanguageTable.language],
                myCode = myCode,
                availableLanguages = emptyList<String>() // This will be set by a sub-resolver if included
            )


            return@transaction task;
        }
    }

    override fun getLanguagesFor(id: Int): List<String> {
        return transaction {
            addLogger(StdOutSqlLogger);
            ProgrammingTaskTable
                .innerJoin(ProgrammingTaskStarterCodeTable)
                .innerJoin(LanguageTable, { LanguageTable.id }, { ProgrammingTaskStarterCodeTable.language })
                .slice(LanguageTable.language, ProgrammingTaskTable.id, ProgrammingTaskStarterCodeTable.id)
                .select {
                    ProgrammingTaskTable.id eq id
                }.withDistinct().map { it[LanguageTable.language] }
        }
    }

    override fun findById(id: Int): ProgrammingTaskEntity? {
        return transaction {
            ProgrammingTaskEntity.findById(id)
        }
    }

    override fun findByIdOrThrow(id: Int): ProgrammingTaskEntity {
        return transaction {
            ProgrammingTaskEntity.findById(id) ?: throw NotFoundException()
        }
    }

    override fun findAll(): List<ProgrammingTaskEntity> {
        return transaction {
            ProgrammingTaskEntity.all().toList()
        }
    }

    override fun create(obj: ProgrammingTask): ProgrammingTaskEntity {
        return transaction {
            ProgrammingTaskEntity.new {
                title = obj.title
                description = obj.description
            }
        }
    }

    override fun update(obj: ProgrammingTask): ProgrammingTaskEntity {
        TODO("Not yet implemented")
    }
}