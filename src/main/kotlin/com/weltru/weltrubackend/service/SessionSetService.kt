
package com.weltru.weltrubackend.service



import com.weltru.weltrubackend.dto.CreateSessionSetRequest
import com.weltru.weltrubackend.dto.UpdateSessionSetRequest
import com.weltru.weltrubackend.entity.SessionSetEntity
import com.weltru.weltrubackend.repository.SessionSetRepository
import com.weltru.weltrubackend.repository.WorkoutSessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class SessionSetService(
    private val sessionSetRepository: SessionSetRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
) {

    @Transactional(readOnly = true)
    fun getSessionSets(sessionId: UUID): List<SessionSetEntity> {
        return sessionSetRepository
            .findAllBySessionIdOrderBySetNumberAsc(sessionId)
    }

    @Transactional
    fun createSessionSet(
        sessionId: UUID,
        request: CreateSessionSetRequest
    ): SessionSetEntity {

        val session = workoutSessionRepository.findById(sessionId)
            .orElseThrow {
                IllegalArgumentException("Workout session not found: $sessionId")
            }

        val sessionSet = SessionSetEntity(
            sessionId = session.id!!,
            splitExerciseId = request.splitExerciseId,
            exerciseId = request.exerciseId,
            exerciseNameSnapshot = request.exerciseNameSnapshot,
            setNumber = request.setNumber,
            targetReps = request.targetReps,
            actualReps = request.actualReps,
            weightKg = request.weightKg,
            durationMinutes = request.durationMinutes
        )

        return sessionSetRepository.save(sessionSet)
    }
    @Transactional(readOnly = true)
    fun getSessionSet(
        sessionId: UUID,
        setId: UUID
    ): SessionSetEntity {
        return sessionSetRepository
            .findByIdAndSessionId(setId, sessionId)
            .orElseThrow {
                NoSuchElementException(
                    "Workout set not found: $setId"
                )
            }
    }
    @Transactional
    fun updateSessionSet(
        sessionId: UUID,
        setId: UUID,
        request: UpdateSessionSetRequest
    ): SessionSetEntity {

        val sessionSet = sessionSetRepository
            .findByIdAndSessionId(setId, sessionId)
            .orElseThrow {
                NoSuchElementException("Workout set not found: $setId")
            }

        request.actualReps?.let {
            sessionSet.actualReps = it
        }

        request.weightKg?.let {
            sessionSet.weightKg = it
        }

        request.durationMinutes?.let {
            sessionSet.durationMinutes = it
        }

        return sessionSetRepository.save(sessionSet)
    }
    @Transactional
    fun completeSessionSet(
        sessionId: UUID,
        setId: UUID
    ): SessionSetEntity {

        val sessionSet = sessionSetRepository
            .findByIdAndSessionId(setId, sessionId)
            .orElseThrow {
                NoSuchElementException(
                    "Workout set not found: $setId"
                )
            }

        sessionSet.isCompleted = true
        sessionSet.completedAt = OffsetDateTime.now()

        return sessionSetRepository.save(sessionSet)
    }
    @Transactional
    fun deleteSessionSet(
        sessionId: UUID,
        setId: UUID
    ) {
        val sessionSet = sessionSetRepository
            .findByIdAndSessionId(setId, sessionId)
            .orElseThrow {
                NoSuchElementException(
                    "Workout set not found: $setId"
                )
            }

        sessionSetRepository.delete(sessionSet)
    }
}
