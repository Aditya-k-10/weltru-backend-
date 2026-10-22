package com.weltru.weltrubackend.service

import com.weltru.weltrubackend.dto.SyncExerciseDto
import com.weltru.weltrubackend.dto.SyncSetDto
import com.weltru.weltrubackend.dto.WorkoutSyncDto
import com.weltru.weltrubackend.entity.SessionSetEntity
import com.weltru.weltrubackend.entity.WorkoutSessionEntity
import com.weltru.weltrubackend.repository.SessionSetRepository
import com.weltru.weltrubackend.repository.WorkoutSessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class WorkoutSessionService(
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val sessionSetRepository: SessionSetRepository
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
            id = id ?: UUID.randomUUID(),
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

    @Transactional
    fun syncWorkoutState(request: WorkoutSyncDto): WorkoutSyncDto {
        val session = workoutSessionRepository.findById(request.sessionId)
            .orElseThrow { IllegalArgumentException("Workout session not found: ${request.sessionId}") }

        session.status = request.status
        session.notes = request.notes
        session.splitNameSnapshot = request.splitNameSnapshot
        if (request.status == "COMPLETED" || request.status == "FINISHED") {
            session.finishedAt = OffsetDateTime.now()
        }
        workoutSessionRepository.save(session)

        // Clear existing sets to allow bulk overwrite
        sessionSetRepository.deleteAllBySessionId(request.sessionId)

        // Bulk insert new sets (Filtering out useless sets and incomplete cardio)
        val newSets = mutableListOf<SessionSetEntity>()
        for (exercise in request.exercises) {
            for (set in exercise.sets) {
                
                // 1. Check if the set is completely empty (Ghost set)
                val isCompletelyEmpty = !set.isCompleted && 
                                        set.weightKg == null && 
                                        set.actualReps == null && 
                                        set.durationMinutes == null

                // 2. Strict Cardio Rule: If it's a Cardio exercise and it is NOT completed, throw it away.
                // We don't save paused/incomplete timers.
                val isIncompleteCardio = exercise.exerciseType == "CARDIO" && !set.isCompleted

                if (!isCompletelyEmpty && !isIncompleteCardio) {
                    newSets.add(
                        SessionSetEntity(
                            id = set.setId ?: UUID.randomUUID(),
                            sessionId = request.sessionId,
                            splitExerciseId = exercise.splitExerciseId,
                            exerciseId = exercise.exerciseId,
                            exerciseNameSnapshot = exercise.exerciseNameSnapshot,
                            setNumber = set.setNumber,
                            weightKg = set.weightKg,
                            actualReps = set.actualReps,
                            durationMinutes = set.durationMinutes,
                            isCompleted = set.isCompleted,
                            // Automatically set timestamp if completed
                            completedAt = if (set.isCompleted) OffsetDateTime.now() else null
                        )
                    )
                }
            }
        }
        sessionSetRepository.saveAll(newSets)

        return getFullWorkoutState(request.sessionId)
    }

    @Transactional(readOnly = true)
    fun getFullWorkoutState(sessionId: UUID): WorkoutSyncDto {
        val session = workoutSessionRepository.findById(sessionId)
            .orElseThrow { IllegalArgumentException("Workout session not found: $sessionId") }

        val sets = sessionSetRepository.findAllBySessionIdOrderBySetNumberAsc(sessionId)

        data class ExerciseKey(val exerciseId: UUID, val splitExerciseId: UUID?, val exerciseNameSnapshot: String)
        
        val setsByExercise = sets.groupBy { 
            ExerciseKey(it.exerciseId, it.splitExerciseId, it.exerciseNameSnapshot) 
        }

        val syncExercises = setsByExercise.map { (key, exerciseSets) ->
            // Infer type: If any set has a duration, it's CARDIO. Otherwise STRENGTH.
            val type = if (exerciseSets.any { it.durationMinutes != null }) "CARDIO" else "STRENGTH"
            
            SyncExerciseDto(
                exerciseId = key.exerciseId,
                splitExerciseId = key.splitExerciseId,
                exerciseNameSnapshot = key.exerciseNameSnapshot,
                exerciseType = type,
                sets = exerciseSets.map {
                    SyncSetDto(
                        setId = it.id,
                        setNumber = it.setNumber,
                        weightKg = it.weightKg,
                        actualReps = it.actualReps,
                        durationMinutes = it.durationMinutes,
                        isCompleted = it.isCompleted
                    )
                }
            )
        }

        return WorkoutSyncDto(
            sessionId = session.id!!,
            splitId = session.splitId,
            splitNameSnapshot = session.splitNameSnapshot,
            status = session.status,
            notes = session.notes,
            exercises = syncExercises
        )
    }
}