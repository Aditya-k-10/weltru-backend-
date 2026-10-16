package com.weltru.weltrubackend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class StartWorkoutRequest(
    @JsonProperty("id")
    val id: UUID? = null,
    @JsonProperty("userId")
    val userId: UUID,
    @JsonProperty("splitId")
    val splitId: UUID? = null,
    @JsonProperty("splitName")
    val splitName: String? = null,
    @JsonProperty("notes")
    val notes: String? = null
)
