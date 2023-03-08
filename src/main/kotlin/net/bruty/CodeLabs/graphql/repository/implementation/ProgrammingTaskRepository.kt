package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.extensions.toUUID
import net.bruty.CodeLabs.graphql.model.*
import net.bruty.CodeLabs.graphql.repository.interfaces.IProgrammingTaskRepository
import net.bruty.CodeLabs.graphql.security.HttpContext
import net.bruty.types.ProgrammingTask
import net.bruty.types.ProgrammingTaskCode
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProgrammingTaskRepository: IProgrammingTaskRepository {
    @Autowired
    lateinit var httpCtx: HttpContext;
    override fun getStarterCodeByLanguage(id: String, language: String): ProgrammingTask {
        return transaction {
            addLogger(StdOutSqlLogger)
            val language = LanguageEntity.find { LanguageTable.language eq language }.firstOrNull() ?: throw NotFoundException()
            val task = ProgrammingTaskEntity.findById(id.toUUID()) ?: throw NotFoundException()
            val code = task.startCodes.firstOrNull { it.language == language } ?: throw NotFoundException()
            val myCode = task.userSubmissions.firstOrNull {
                it.createdBy.id.value == (httpCtx.principal?.userUUID ?: throw UnauthorisedException())
            }
            ProgrammingTask(
                id = task.id.toString(),
                description = task.description,
                title = task.title,
                code = ProgrammingTaskCode(
                    starterCode = code.starterCode,
                    testCode = code.unitTestCode,
                    myCode = myCode?.codeText ?: code.starterCode,
                    language = language.language
                ),
                files = task.files.toList().map { it.toDTO() }
            )
        }
    }

    override fun getStarterCodeDefault(id: String): ProgrammingTask {
        val language = transaction {
            val task = ProgrammingTaskEntity.findById(id.toUUID()) ?: throw NotFoundException()
            task.defaultLanguage
        };
        return getStarterCodeByLanguage(id, language.language)
    }

    override fun getLanguagesFor(id: String): List<String> {
        return transaction {
            addLogger(StdOutSqlLogger);
            ProgrammingTaskTable
                .innerJoin(ProgrammingTaskStarterCodeTable)
                .innerJoin(LanguageTable, { LanguageTable.id }, { ProgrammingTaskStarterCodeTable.language })
                .slice(LanguageTable.language, ProgrammingTaskTable.id, ProgrammingTaskStarterCodeTable.id)
                .select {
                    ProgrammingTaskTable.id eq id.toUUID()
                }.withDistinct().map { it[LanguageTable.language] }
        }
    }

    override fun findById(id: String): ProgrammingTaskEntity? {
        return transaction {
            ProgrammingTaskEntity.findById(id.toUUID())
        }
    }

    override fun findByIdOrThrow(id: String): ProgrammingTaskEntity {
        return transaction {
            addLogger(StdOutSqlLogger)
            ProgrammingTaskEntity.findById(id.toUUID()) ?: throw NotFoundException()
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