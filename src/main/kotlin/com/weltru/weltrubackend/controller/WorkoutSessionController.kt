package com.weltru.weltrubackend.controller

import com.weltru.weltrubackend.dto.StartWorkoutRequest
import com.weltru.weltrubackend.dto.WorkoutSyncDto
import com.weltru.weltrubackend.entity.WorkoutSessionEntity
import com.weltru.weltrubackend.service.WorkoutSessionService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/workout-sessions")
class WorkoutSessionController(
    private val workoutSessionService: WorkoutSessionService
) {
    private val logger = LoggerFactory.getLogger(WorkoutSessionController::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun startWorkout(
        @RequestBody request: StartWorkoutRequest
    ): WorkoutSessionEntity {
        logger.info("📩 [CONTROLLER POST /api/workout-sessions] Start Workout Request: SessionID=${request.id}, UserId=${request.userId}, SplitName='${request.splitName}'")
        return workoutSessionService.startWorkout(
            id = request.id,
            userId = request.userId,
            splitId = request.splitId,
            splitNameSnapshot = request.splitName,
            notes = request.notes
        )
    }

    @PatchMapping("/{sessionId}/complete")
    fun completeWorkout(
        @PathVariable sessionId: UUID
    ): WorkoutSessionEntity {
        logger.info("📩 [CONTROLLER PATCH /api/workout-sessions/$sessionId/complete] Complete Workout Request")
        return workoutSessionService.completeWorkout(sessionId)
    }

    @GetMapping("/{sessionId}")
    fun getWorkoutSession(
        @PathVariable sessionId: UUID
    ): WorkoutSessionEntity {
        logger.info("📩 [CONTROLLER GET /api/workout-sessions/$sessionId] Get Workout Session Request")
        return workoutSessionService.getWorkoutSession(sessionId)
    }

    @GetMapping("/{sessionId}/state")
    fun getWorkoutState(
        @PathVariable sessionId: UUID
    ): WorkoutSyncDto {
        logger.info("📩 [CONTROLLER GET /api/workout-sessions/$sessionId/state] Get Full Workout State Request")
        return workoutSessionService.getFullWorkoutState(sessionId)
    }

    @PutMapping("/{sessionId}/sync")
    fun syncWorkoutState(
        @PathVariable sessionId: UUID,
        @RequestBody request: WorkoutSyncDto
    ): WorkoutSyncDto {
        logger.info("📩 [CONTROLLER PUT /api/workout-sessions/$sessionId/sync] Sync Workout State Request: Status=${request.status}, ExercisesCount=${request.exercises.size}")
        return workoutSessionService.syncWorkoutState(request)
    }
}
