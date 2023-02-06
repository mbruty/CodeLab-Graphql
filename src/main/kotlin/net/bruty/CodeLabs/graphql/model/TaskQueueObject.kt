package net.bruty.CodeLabs.graphql.model

import kotlinx.serialization.Serializable
import org.springframework.data.redis.core.RedisHash



@RedisHash("Task")
@Serializable
data class TaskQueueObject (val id: String, val retryCount: Int);