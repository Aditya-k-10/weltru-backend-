package com.weltru.weltrubackend.dto

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class WorkoutSyncDto(
    val sessionId: UUID,
    val splitId: String? = null,
    val splitNameSnapshot: String? = null,
    val status: String,
    val startedAt: OffsetDateTime? = null,
    val finishedAt: OffsetDateTime? = null,
    val notes: String? = null,
    val calories: BigDecimal? = null,
    val exercises: List<SyncExerciseDto> = emptyList()
)

data class SyncExerciseDto(
    val exerciseId: String,
    val splitExerciseId: UUID? = null,
    val exerciseNameSnapshot: String? = null,
    val exerciseType: String? = "STRENGTH",
    val exerciseOrder: Int? = null,
    val targetSets: Int? = null,
    val targetDurationMinutes: Int? = null,
    val exerciseStatus: String? = "IN_PROGRESS",
    val sets: List<SyncSetDto> = emptyList()
)

data class SyncSetDto(
    val setId: UUID? = null,
    val setNumber: Int,
    val targetReps: Int? = null,
    val actualReps: Int? = null,
    val weightKg: BigDecimal? = null,
    val durationMinutes: Int? = null,
    val isCompleted: Boolean = false
)