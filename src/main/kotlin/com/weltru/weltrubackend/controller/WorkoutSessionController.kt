package com.weltru.weltrubackend.controller

import com.weltru.weltrubackend.dto.StartWorkoutRequest
import com.weltru.weltrubackend.dto.WorkoutSyncDto
import com.weltru.weltrubackend.entity.WorkoutSessionEntity
import com.weltru.weltrubackend.service.WorkoutSessionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/workout-sessions")
class WorkoutSessionController(
    private val workoutSessionService: WorkoutSessionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun startWorkout(
        @RequestBody request: StartWorkoutRequest
    ): WorkoutSessionEntity {
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
        return workoutSessionService.completeWorkout(sessionId)
    }

    @GetMapping("/{sessionId}")
    fun getWorkoutSession(
        @PathVariable sessionId: UUID
    ): WorkoutSessionEntity {
        return workoutSessionService.getWorkoutSession(sessionId)
    }

    @GetMapping("/{sessionId}/state")
    fun getWorkoutState(
        @PathVariable sessionId: UUID
    ): WorkoutSyncDto {
        return workoutSessionService.getFullWorkoutState(sessionId)
    }

    @PutMapping("/{sessionId}/sync")
    fun syncWorkoutState(
        @PathVariable sessionId: UUID,
        @RequestBody request: WorkoutSyncDto
    ): WorkoutSyncDto {
        return workoutSessionService.syncWorkoutState(request)
    }
}
