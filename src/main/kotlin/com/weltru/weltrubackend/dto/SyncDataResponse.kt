package com.weltru.weltrubackend.dto

import com.weltru.weltrubackend.entity.SessionSetEntity
import com.weltru.weltrubackend.entity.SplitExerciseEntity
import com.weltru.weltrubackend.entity.WorkoutSessionEntity
import java.util.UUID

data class SyncDataResponse(
    val splits: List<SplitWithExercisesDto>,
    val recentSessions: List<SessionWithSetsDto>
)

data class SplitWithExercisesDto(
    val splitId: UUID,
    val name: String,
    val description: String?,
    val exercises: List<SplitExerciseEntity>
)

data class SessionWithSetsDto(
    val session: WorkoutSessionEntity,
    val sets: List<SessionSetEntity>
)
