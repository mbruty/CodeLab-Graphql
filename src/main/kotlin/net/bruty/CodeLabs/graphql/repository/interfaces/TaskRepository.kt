package net.bruty.CodeLabs.graphql.repository.interfaces

import net.bruty.CodeLabs.graphql.model.TaskQueueObject
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface TaskRepository: CrudRepository<TaskQueueObject?, String?>