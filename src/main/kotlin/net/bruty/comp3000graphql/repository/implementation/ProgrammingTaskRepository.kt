package net.bruty.comp3000graphql.repository.implementation

import net.bruty.comp3000graphql.City.Companion.wrapRows
import net.bruty.comp3000graphql.exceptions.NotFoundException
import net.bruty.comp3000graphql.model.LanguageTable
import net.bruty.comp3000graphql.model.ProgrammingTaskEntity
import net.bruty.comp3000graphql.model.ProgrammingTaskStarterCodeTable
import net.bruty.comp3000graphql.model.ProgrammingTaskTable
import net.bruty.comp3000graphql.repository.interfaces.IProgrammingTaskRepository
import net.bruty.types.ProgrammingTask
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component

@Component
class ProgrammingTaskRepository: IProgrammingTaskRepository {
    override fun getStarterCodeByLanguage(id: Int, language: String): ProgrammingTask {
        return transaction {
            addLogger(StdOutSqlLogger)
            val x = ProgrammingTaskEntity.wrapRows(ProgrammingTaskTable
                .innerJoin(ProgrammingTaskStarterCodeTable)
                .innerJoin(LanguageTable)
                .select {
                    ProgrammingTaskTable.id eq id and (LanguageTable.language eq language)
                }
            ).firstOrNull() ?: throw NotFoundException()

            ProgrammingTask(
                title = x.title,
                description = x.description,
                starterCode = x.startCodes.first().starterCode,
                testCode = x.startCodes.first().unitTestCode
            )
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