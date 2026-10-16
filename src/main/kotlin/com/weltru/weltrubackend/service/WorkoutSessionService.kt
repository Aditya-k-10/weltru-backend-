package com.weltru.weltrubackend.service

import com.weltru.weltrubackend.entity.WorkoutSessionEntity
import com.weltru.weltrubackend.repository.WorkoutSessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class WorkoutSessionService(
    private val workoutSessionRepository: WorkoutSessionRepository
) {

    @Transactional
    fun startWorkout(
        id: UUID? = null,
        userId: UUID,
        splitId: UUID? = null,
        splitNameSnapshot: String? = null,
        notes: String? = null
    ): WorkoutSessionEntity {
        if (id != null) {
            val existing = workoutSessionRepository.findById(id)
            if (existing.isPresent) {
                return existing.get()
            }
        }

        val session = WorkoutSessionEntity(
            id = id,
            userId = userId,
            splitId = splitId,
            splitNameSnapshot = splitNameSnapshot,
            status = "IN_PROGRESS",
            notes = notes
        )

        return workoutSessionRepository.save(session)
    }

    @Transactional
    fun completeWorkout(sessionId: UUID): WorkoutSessionEntity {
        val session = workoutSessionRepository.findById(sessionId)
            .orElseThrow {
                IllegalArgumentException(
                    "Workout session not found: $sessionId"
                )
            }

        require(session.status == "IN_PROGRESS") {
            "Only an in-progress workout can be completed"
        }

        session.status = "COMPLETED"
        session.finishedAt = OffsetDateTime.now()

        return workoutSessionRepository.save(session)
    }

    @Transactional(readOnly = true)
    fun getWorkoutSession(sessionId: UUID): WorkoutSessionEntity {
        return workoutSessionRepository.findById(sessionId)
            .orElseThrow {
                IllegalArgumentException(
                    "Workout session not found: $sessionId"
                )
            }
    }
}
