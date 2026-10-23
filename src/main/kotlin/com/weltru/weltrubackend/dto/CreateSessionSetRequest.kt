package com.weltru.weltrubackend.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateSessionSetRequest(
    val splitExerciseId: UUID? = null,
    val exerciseId: String,
    val exerciseNameSnapshot: String,
    val setNumber: Int,
    val targetReps: Int? = null,
    val actualReps: Int? = null,
    val weightKg: BigDecimal? = null,
    val durationMinutes: Int? = null
)
