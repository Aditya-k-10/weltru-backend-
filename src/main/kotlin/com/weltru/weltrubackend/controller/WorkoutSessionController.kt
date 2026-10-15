package com.weltru.weltrubackend.controller
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
        @RequestParam userId: UUID,
        @RequestParam(required = false) splitId: UUID?,
        @RequestParam(required = false) splitName: String?,
        @RequestParam(required = false) notes: String?
    ):WorkoutSessionEntity{
        return workoutSessionService.startWorkout(
            userId = userId,
            splitId = splitId,
            splitNameSnapshot = splitName,
            notes = notes
        )
    }
}