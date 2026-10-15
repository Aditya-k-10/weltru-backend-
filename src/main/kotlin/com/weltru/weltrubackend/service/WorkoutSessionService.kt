package com.weltru.weltrubackend.service

import com.weltru.weltrubackend.repository.WorkoutSessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class WorkoutSessionService(
    private val workoutSessionRepository: WorkoutSessionRepository
) {

    @Transactional
    fun startWorkout(
        userId: UUID,
        splitId: UUID? = null,
        splitNameSnapshot: String? = null,
        notes: String? = null
    ): WorkoutSessionEntity {

        val session = WorkoutSessionEntity(
            userId = userId,
            splitId = splitId,
            splitNameSnapshot = splitNameSnapshot,
            status = "IN_PROGRESS",
            notes = notes
        )

        return workoutSessionRepository.save(session)
    }
}