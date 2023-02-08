package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.exceptions.AlreadyExistsException
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.model.*
import net.bruty.CodeLabs.graphql.repository.interfaces.IModuleRepository
import net.bruty.types.Module
import net.bruty.types.ProgrammingTask
import net.bruty.types.User
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component

@Component
class ModuleRepository: IModuleRepository {
    override fun getTasks(id: Int): List<ProgrammingTask> {
        return transaction {
            val entity = ModuleEntity.findById(id) ?: throw NotFoundException();
            return@transaction entity.tasks.toList().map {
                ProgrammingTask(
                    id = it.id.value.toString(),
                    title = it.title,
                    description = it.description
                );
            }
        }
    }

    override fun getCreatedBy(id: Int): User {
        return transaction {
            val module = findByIdOrThrow(id);
            val found = module.createdBy;
            return@transaction User(
                id = found.id.value,
                email = "redacted",
                username = found.username,
                password = "redacted",
                xp = -1,
                refreshCount = -1
            );
        }

    }

    override fun getCompletedPct(id: Int, userId: Int): Float {
        val completed = transaction {
            UsersTable
                .innerJoin(UserCodeSubmissionTable)
                .innerJoin(ProgrammingTaskTable)
                .innerJoin(ModuleTaskTable)
                .innerJoin(ModuleTable, { ModuleTable.id }, { ModuleTaskTable.module })
                .slice(UserCodeSubmissionTable.id.count())
                .select {
                    UsersTable.id eq userId and
                            (UserCodeSubmissionTable.isSubmitted) and
                            (ModuleTable.id eq id)
                }
                .groupBy(ProgrammingTaskTable.id)
                .count()

        }

        val total = transaction {
            ModuleTable
                .innerJoin(ModuleTaskTable)
                .innerJoin(ProgrammingTaskTable)
                .slice(ModuleTaskTable.module.count())
                .select { ModuleTable.id eq id }
                .groupBy(ProgrammingTaskTable.id)
                .count()
        }

        return ((completed / total) * 100).toFloat();
    }

    override fun link(moduleID: Int, taskID: Int, userID: Int) {
        transaction {
            addLogger(StdOutSqlLogger)
            val foundModule = ModuleEntity.findById(moduleID) ?: throw NotFoundException()

            if(foundModule.createdBy.id.value != userID) throw UnauthorisedException()

            val maxOrder = ModuleTaskTable
                .slice(ModuleTaskTable.order.max())
                .select {
                    ModuleTaskTable.module eq foundModule.id
                }
                .groupBy(ModuleTaskTable.module)
                .firstOrNull()

            val notNullOrder = maxOrder?.getOrNull(ModuleTaskTable.order) ?: -1

            try {
                ModuleTaskTable.insert {
                    it[order] = notNullOrder + 1
                    it[module] = foundModule.id
                    it[task] = taskID
                }
            } catch (e: ExposedSQLException) {
                throw AlreadyExistsException()
            }

        }
    }

    override fun findEnrolled(userId: Int): List<Module> {
        return transaction {
            val user = UserEntity.findById(userId) ?: throw UnauthorisedException();
            user.modules.toList().map { it.toDTO() }
        }
    }

    override fun findById(id: Int): ModuleEntity? {
        return transaction {
            ModuleEntity.findById(id)
        }
    }

    override fun findByIdOrThrow(id: Int): ModuleEntity {
        return transaction {
            ModuleEntity.findById(id) ?: throw NotFoundException()
        }
    }

    override fun findAll(): List<ModuleEntity> {
        return transaction {
            ModuleEntity.all().with(ModuleEntity::tasks).toList()
        }
    }

    override fun create(obj: Module): ModuleEntity {
        return transaction {
            val user = obj.createdBy?.id?.let { UserEntity.findById(it) }
            user ?: throw UnauthorisedException()
            ModuleEntity.new {
                title = obj.title
                description = obj.description
                createdBy = user
            }
        }
    }

    override fun update(obj: Module): ModuleEntity {
        TODO("Not yet implemented")
    }
}