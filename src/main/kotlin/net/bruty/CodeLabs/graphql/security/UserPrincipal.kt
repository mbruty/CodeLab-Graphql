package net.bruty.CodeLabs.graphql.security

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class UserPrincipal(
    @JsonProperty("userId")
    override val userId: String,
    @JsonProperty("userUUID")
    override val userUUID: UUID,
    @JsonProperty("refreshCount")
    override val refreshCount: Int
): IPrincipal
