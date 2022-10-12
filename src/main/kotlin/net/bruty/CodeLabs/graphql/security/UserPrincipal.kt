package net.bruty.CodeLabs.graphql.security

import com.fasterxml.jackson.annotation.JsonProperty

data class UserPrincipal(
    @JsonProperty("userId")
    override val userId: Int,
    @JsonProperty("refreshCount")
    override val refreshCount: Int
): IPrincipal
