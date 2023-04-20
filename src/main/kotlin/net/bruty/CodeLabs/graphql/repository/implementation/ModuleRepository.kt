package net.bruty.CodeLabs.graphql.repository.implementation

import net.bruty.CodeLabs.graphql.exceptions.AlreadyExistsException
import net.bruty.CodeLabs.graphql.exceptions.NotFoundException
import net.bruty.CodeLabs.graphql.exceptions.UnauthorisedException
import net.bruty.CodeLabs.graphql.extensions.toUUID
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
    override fun getTasks(id: String): List<ProgrammingTask> {
        return transaction {
            val entity = ModuleEntity.findById(id.toUUID()) ?: throw NotFoundException();
            return@transaction entity.tasks.toList().map {
                ProgrammingTask(
                    id = it.id.value.toString(),
                    title = it.title,
                    description = it.description
                );
            }
        }
    }

    override fun getCreatedBy(id: String): User {
        return transaction {
            val module = findByIdOrThrow(id);
            val found = module.createdBy;
            return@transaction User(
                id = found.id.toString(),
                email = "redacted",
                username = found.username,
                password = "redacted",
                xp = -1,
                refreshCount = -1
            );
        }

    }

    override fun getCompletedPct(id: String, userId: String): Float {
        val completed = transaction {
            UsersTable
                .innerJoin(UserCodeSubmissionTable)
                .innerJoin(ProgrammingTaskTable)
                .innerJoin(ModuleTaskTable)
                .innerJoin(ModuleTable, { ModuleTable.id }, { ModuleTaskTable.module })
                .slice(UserCodeSubmissionTable.id.count())
                .select {
                    UsersTable.id eq userId.toUUID() and
                            (UserCodeSubmissionTable.isCompleted) and
                            (ModuleTable.id eq id.toUUID())
                }
                .groupBy(ProgrammingTaskTable.id)
                .count()

        }

        val total = transaction {
            ModuleTable
                .innerJoin(ModuleTaskTable)
                .innerJoin(ProgrammingTaskTable)
                .slice(ModuleTaskTable.module.count())
                .select { ModuleTable.id eq id.toUUID() }
                .groupBy(ProgrammingTaskTable.id)
                .count()
        }

        return ((completed / total) * 100).toFloat();
    }

    override fun link(moduleID: String, taskID: String, userID: String) {
        transaction {
            val module = ModuleEntity.findById(moduleID.toUUID()) ?: throw NotFoundException()
            val task = ProgrammingTaskEntity.findById(taskID.toUUID()) ?: throw NotFoundException()

            if(module.createdBy.id.value != userID.toUUID()) throw UnauthorisedException()

            val tasks = module.tasks.toMutableList()
            tasks.add(task)
            module.tasks = SizedCollection(tasks);
        }
    }

    override fun findEnrolled(userId: String): List<Module> {
        return transaction {
            val user = UserEntity.findById(userId.toUUID()) ?: throw UnauthorisedException();
            user.modules.toList().map { it.toDTO() }
        }
    }

    override fun findById(id: String): ModuleEntity? {
        return transaction {
            ModuleEntity.findById(id.toUUID())
        }
    }

    override fun findByIdOrThrow(id: String): ModuleEntity {
        return transaction {
            ModuleEntity.findById(id.toUUID()) ?: throw NotFoundException()
        }
    }

    override fun findAll(): List<ModuleEntity> {
        return transaction {
            ModuleEntity.all().with(ModuleEntity::tasks).toList()
        }
    }

    override fun create(obj: Module): ModuleEntity {
        return transaction {
            val user = obj.createdBy?.id?.let { UserEntity.findById(it.toUUID()) }
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