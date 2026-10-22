package com.weltru.weltrubackend.dto

import java.math.BigDecimal
import java.util.UUID

// Request & Response payload for bulk syncing a workout session
data class WorkoutSyncDto(
    val sessionId: UUID,
    val splitId: UUID? = null,
    val splitNameSnapshot: String? = null,
    val status: String, // "STARTED", "PAUSED", "FINISHED"
    val notes: String? = null,
    val exercises: List<SyncExerciseDto>
)

data class SyncExerciseDto(
    val exerciseId: UUID,
    val splitExerciseId: UUID? = null, // Null if added ad-hoc during workout
    val exerciseNameSnapshot: String,
    val exerciseType: String, // "STRENGTH" or "CARDIO"
    val sets: List<SyncSetDto>
)

data class SyncSetDto(
    val setId: UUID? = null, // Can be null if it's a completely new set added on UI
    val setNumber: Int,
    val weightKg: BigDecimal? = null,
    val actualReps: Int? = null,
    val durationMinutes: Int? = null,
    val isCompleted: Boolean = false
)
