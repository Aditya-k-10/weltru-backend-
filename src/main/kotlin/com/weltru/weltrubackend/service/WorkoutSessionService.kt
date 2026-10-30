package com.weltru.weltrubackend.service

import com.weltru.weltrubackend.dto.SyncExerciseDto
import com.weltru.weltrubackend.dto.SyncSetDto
import com.weltru.weltrubackend.dto.WorkoutSyncDto
import com.weltru.weltrubackend.entity.SessionSetEntity
import com.weltru.weltrubackend.entity.WorkoutSessionEntity
import com.weltru.weltrubackend.repository.SessionSetRepository
import com.weltru.weltrubackend.repository.SplitExerciseRepository
import com.weltru.weltrubackend.repository.WorkoutSessionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Service
class WorkoutSessionService(
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val sessionSetRepository: SessionSetRepository,
    private val splitExerciseRepository: SplitExerciseRepository
) {
    private val logger = LoggerFactory.getLogger(WorkoutSessionService::class.java)

    // exerciseStates map value format: "STATUS|NAME|TYPE"
    private val stateSeparator = "|"

    @Transactional(readOnly = true)
    fun getAllSessionsForUser(userId: UUID): List<WorkoutSyncDto> {
        val sessions = workoutSessionRepository.findAllByUserIdOrderByStartedAtDesc(userId)
        return sessions.map { session ->
            getFullWorkoutState(session.id!!)
        }
    }

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
                logger.info("startWorkout: returning existing session ID=$id")
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
        logger.info("startWorkout: created session ID=${session.id}")
        return workoutSessionRepository.save(session)
    }

    @Transactional
    fun completeWorkout(sessionId: UUID): WorkoutSessionEntity {
        val session = workoutSessionRepository.findById(sessionId)
            .orElseThrow { IllegalArgumentException("Workout session not found: $sessionId") }

        require(session.status == "IN_PROGRESS") {
            "Only an in-progress workout can be completed"
        }

        session.status = "COMPLETED"
        session.finishedAt = OffsetDateTime.now()
        logger.info("completeWorkout: session $sessionId completed at ${session.finishedAt}")
        return workoutSessionRepository.save(session)
    }

    @Transactional(readOnly = true)
    fun getWorkoutSession(sessionId: UUID): WorkoutSessionEntity {
        return workoutSessionRepository.findById(sessionId)
            .orElseThrow { IllegalArgumentException("Workout session not found: $sessionId") }
    }

    @Transactional
    fun syncWorkoutState(request: WorkoutSyncDto): WorkoutSyncDto {
        logger.info("SYNC start: session=${request.sessionId}, status=${request.status}, exercises=${request.exercises.size}")

        // 1. session load, status/notes/splitNameSnapshot set.
        val session = workoutSessionRepository.findById(request.sessionId)
            .orElseThrow { IllegalArgumentException("Workout session not found: ${request.sessionId}") }

        session.status = request.status
        session.notes = request.notes
        session.splitNameSnapshot = request.splitNameSnapshot
        if (request.calories != null) {
            session.calories = request.calories
            logger.info("🔥 [BACKEND CALORIES SAVED] Session ID={} -> Saved calories: {} kcal into database", session.id, session.calories)
        } else {
            logger.info("ℹ️ [BACKEND CALORIES] No calories provided in sync request for Session ID={}", session.id)
        }

        // 2. finishedAt: COMPLETED/FINISHED par now() (agar null), warna null.
        val isFinal = request.status == "COMPLETED" || request.status == "FINISHED"
        if (isFinal) {
            if (session.finishedAt == null) session.finishedAt = OffsetDateTime.now()
        } else {
            session.finishedAt = null
        }

        // Template exercises fallback if needed
        val templateExercises = if (session.splitId != null) {
            splitExerciseRepository.findAllBySplitIdOrderByExerciseOrder(session.splitId!!)
        } else emptyList()
        val templateByExId = templateExercises.associateBy { it.exerciseId }
        val templateByName = templateExercises.associateBy { it.exerciseName.trim().lowercase() }

        // 3. exerciseStates = request.exercises.associate { exerciseId to "status|name|type|targetDurationMinutes" }
        session.exerciseStates = request.exercises.associate { exercise ->
            val status = exercise.exerciseStatus?.takeIf { it.isNotBlank() } ?: "IN_PROGRESS"
            val name = exercise.exerciseNameSnapshot?.takeIf { it.isNotBlank() } ?: exercise.exerciseId
            val type = exercise.exerciseType ?: "STRENGTH"
            val targetMinutes = exercise.targetDurationMinutes?.toString()
                ?: (if (type == "CARDIO") {
                    templateByExId[exercise.exerciseId]?.targetDurationMinutes?.toString()
                        ?: templateByName[name.trim().lowercase()]?.targetDurationMinutes?.toString()
                        ?: "10"
                } else "")
            exercise.exerciseId to listOf(status, name, type, targetMinutes).joinToString(stateSeparator)
        }

        // 4. workoutSessionRepository.save(session)
        workoutSessionRepository.save(session)

        // 5. sessionSetRepository.deleteAllBySessionId(id); sessionSetRepository.flush()
        sessionSetRepository.deleteAllBySessionId(request.sessionId)
        sessionSetRepository.flush()

        // 6. Sets insert: id = UUID.randomUUID() (client setId ignore). Empty sets aur incomplete cardio skip.
        val newSets = mutableListOf<SessionSetEntity>()
        for (exercise in request.exercises) {
            val isCardio = exercise.exerciseType == "CARDIO"
            val exerciseName = exercise.exerciseNameSnapshot?.takeIf { it.isNotBlank() } ?: exercise.exerciseId

            for (set in exercise.sets) {
                val hasWeight = set.weightKg != null && set.weightKg.compareTo(BigDecimal.ZERO) != 0
                val hasReps = (set.actualReps ?: 0) > 0
                val hasDuration = (set.durationMinutes ?: 0) > 0
                val isEmpty = !set.isCompleted && !hasWeight && !hasReps && !hasDuration

                if (isEmpty) {
                    logger.info("SKIP set #${set.setNumber} for ${exercise.exerciseId}: empty=$isEmpty")
                    continue
                }

                val rawDuration = set.durationMinutes
                logger.info("⏱️ [BACKEND DURATION SECONDS] Exercise '$exerciseName', Set #${set.setNumber} -> Stored raw seconds = $rawDuration")

                newSets.add(
                    SessionSetEntity(
                        id = UUID.randomUUID(),
                        sessionId = request.sessionId,
                        splitExerciseId = exercise.splitExerciseId,
                        exerciseId = exercise.exerciseId,
                        exerciseNameSnapshot = exerciseName,
                        setNumber = set.setNumber,
                        weightKg = set.weightKg,
                        actualReps = set.actualReps,
                        durationMinutes = rawDuration,
                        isCompleted = set.isCompleted,
                        completedAt = if (set.isCompleted) OffsetDateTime.now() else null
                    )
                )
            }
        }

        // 7. saveAll(newSets); return getFullWorkoutState(id).
        sessionSetRepository.saveAll(newSets)
        logger.info("SYNC done: saved ${newSets.size} sets for session ${request.sessionId}")

        return getFullWorkoutState(request.sessionId)
    }

    @Transactional(readOnly = true)
    fun getFullWorkoutState(sessionId: UUID): WorkoutSyncDto {
        val sessionOpt = workoutSessionRepository.findById(sessionId)
        if (sessionOpt.isEmpty) {
            return WorkoutSyncDto(
                sessionId = sessionId,
                splitId = null,
                splitNameSnapshot = "Workout Session",
                status = "IN_PROGRESS",
                startedAt = OffsetDateTime.now(),
                finishedAt = null,
                exercises = emptyList()
            )
        }
        val session = sessionOpt.get()

        val sets = sessionSetRepository.findAllBySessionIdOrderBySetNumberAsc(sessionId)
        val setsByExerciseId = sets.groupBy { it.exerciseId }

        // If exerciseStates is empty and no sets exist yet for this session, load template exercises from split_exercises
        val hasSessionData = session.exerciseStates.isNotEmpty() || sets.isNotEmpty()
        if (!hasSessionData && session.splitId != null) {
            val templateExercises = splitExerciseRepository.findAllBySplitIdOrderByExerciseOrder(session.splitId!!)
            val syncExercises = templateExercises.map { templateEx ->
                val targetSetsCount = templateEx.targetSets ?: 3
                val isCardio = templateEx.exerciseType == "CARDIO"
                val exId = templateEx.exerciseId

                SyncExerciseDto(
                    exerciseId = exId,
                    splitExerciseId = templateEx.id,
                    exerciseNameSnapshot = templateEx.exerciseName,
                    exerciseType = templateEx.exerciseType,
                    exerciseStatus = "IN_PROGRESS",
                    targetSets = templateEx.targetSets,
                    targetDurationMinutes = templateEx.targetDurationMinutes,
                    sets = if (isCardio) emptyList() else (1..targetSetsCount).map { setNum ->
                        SyncSetDto(
                            setId = null,
                            setNumber = setNum,
                            targetReps = templateEx.targetRepsMin,
                            actualReps = null,
                            weightKg = null,
                            durationMinutes = templateEx.targetDurationMinutes,
                            isCompleted = false
                        )
                    }
                )
            }

            return WorkoutSyncDto(
                sessionId = session.id!!,
                splitId = session.splitId?.toString(),
                splitNameSnapshot = session.splitNameSnapshot,
                status = session.status,
                startedAt = session.startedAt,
                finishedAt = session.finishedAt,
                notes = session.notes,
                calories = session.calories,
                exercises = syncExercises
            )
        }

        val allExerciseIds = (session.exerciseStates.keys + setsByExerciseId.keys).distinct()
        val templateExercises = if (session.splitId != null) {
            splitExerciseRepository.findAllBySplitIdOrderByExerciseOrder(session.splitId!!)
        } else emptyList()
        val templateByExId = templateExercises.associateBy { it.exerciseId }
        val templateByName = templateExercises.associateBy { it.exerciseName.trim().lowercase() }

        val syncExercises = allExerciseIds.map { exId ->
            val exerciseSets = setsByExerciseId[exId].orEmpty()
            val firstSet = exerciseSets.firstOrNull()

            val parts = (session.exerciseStates[exId] ?: "IN_PROGRESS").split(stateSeparator, limit = 4)
            val exerciseStatus = parts.getOrNull(0)?.takeIf { it.isNotBlank() } ?: "IN_PROGRESS"
            val nameFromState = parts.getOrNull(1)?.takeIf { it.isNotBlank() }
            val typeFromState = parts.getOrNull(2)?.takeIf { it.isNotBlank() }
            val targetMinutesFromState = parts.getOrNull(3)?.toIntOrNull()
                ?: templateByExId[exId]?.targetDurationMinutes
                ?: templateByName[nameFromState?.trim()?.lowercase()]?.targetDurationMinutes
                ?: if (typeFromState == "CARDIO" || exerciseSets.any { it.durationMinutes != null }) 10 else null

            val exerciseNameSnapshot = firstSet?.exerciseNameSnapshot ?: nameFromState ?: exId
            val exerciseType = typeFromState
                ?: if (exerciseSets.any { it.durationMinutes != null }) "CARDIO" else "STRENGTH"

            SyncExerciseDto(
                exerciseId = exId,
                splitExerciseId = firstSet?.splitExerciseId,
                exerciseNameSnapshot = exerciseNameSnapshot,
                exerciseType = exerciseType,
                exerciseStatus = exerciseStatus,
                targetDurationMinutes = targetMinutesFromState,
                sets = exerciseSets.map { s ->
                    SyncSetDto(
                        setId = s.id,
                        setNumber = s.setNumber,
                        targetReps = s.targetReps,
                        actualReps = s.actualReps,
                        weightKg = s.weightKg,
                        durationMinutes = s.durationMinutes,
                        isCompleted = s.isCompleted
                    )
                }
            )
        }

        logger.info("🔥 [BACKEND CALORIES FETCHED] Session ID={} -> Returning calories: {} kcal", session.id, session.calories)
        return WorkoutSyncDto(
            sessionId = session.id!!,
            splitId = session.splitId?.toString(),
            splitNameSnapshot = session.splitNameSnapshot,
            status = session.status,
            startedAt = session.startedAt,
            finishedAt = session.finishedAt,
            notes = session.notes,
            calories = session.calories,
            exercises = syncExercises
        )}}