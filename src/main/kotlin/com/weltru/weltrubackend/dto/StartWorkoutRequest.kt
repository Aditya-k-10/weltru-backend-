package com.weltru.weltrubackend.dto

import java.util.UUID

data class StartWorkoutRequest(
    val userId: UUID,
    val splitId: UUID? = null,
    val splitName: String? = null,
    val notes: String? = null
)
