package com.weltru.weltrubackend.controller

import com.weltru.weltrubackend.entity.SplitExerciseEntity
import com.weltru.weltrubackend.service.SplitExerciseService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/splits/{splitId}/exercises")
class SplitExerciseController(
    private val splitExerciseService: SplitExerciseService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addExercise(
        @PathVariable splitId: UUID,
        @RequestBody request: SplitExerciseRequest
    ): SplitExerciseEntity {

        val type = request.exerciseType.uppercase()

        require(type == "STRENGTH" || type == "CARDIO") {
            "exerciseType must be STRENGTH or CARDIO"
        }

        if (type == "STRENGTH") {
            require(request.targetSets != null && request.targetSets > 0) {
                "targetSets must be greater than 0 for strength exercises"
            }

            require(request.targetRepsMin != null && request.targetRepsMin > 0) {
                "targetRepsMin must be greater than 0 for strength exercises"
            }

            require(
                request.targetRepsMax != null &&
                        request.targetRepsMax >= request.targetRepsMin
            ) {
                "targetRepsMax must be greater than or equal to targetRepsMin"
            }
        }

        if (type == "CARDIO") {
            require(
                request.targetDurationMinutes != null &&
                        request.targetDurationMinutes > 0
            ) {
                "targetDurationMinutes must be greater than 0 for cardio exercises"
            }
        }

        val exercise = SplitExerciseEntity(
            splitId = splitId,
            exerciseId = request.exerciseId,
            exerciseName = request.exerciseName,
            exerciseType = type,
            exerciseOrder = request.exerciseOrder,
            targetSets = if (type == "STRENGTH") request.targetSets else null,
            targetRepsMin = if (type == "STRENGTH") request.targetRepsMin else null,
            targetRepsMax = if (type == "STRENGTH") request.targetRepsMax else null,
            targetDurationMinutes =
                if (type == "CARDIO") request.targetDurationMinutes else null
        )

        return splitExerciseService.addExercise(splitId, exercise)
    }

    @GetMapping
    fun getExercises(
        @PathVariable splitId: UUID
    ): List<SplitExerciseEntity> {
        return splitExerciseService.getExercisesBySplitId(splitId)
    }

    @DeleteMapping("/{splitExerciseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteExercise(
        @PathVariable splitId: UUID,
        @PathVariable splitExerciseId: UUID
    ) {
        splitExerciseService.deleteExercise(
            splitId,
            splitExerciseId
        )
    }

    @PutMapping("/{splitExerciseId}")
    fun updateExercise(
        @PathVariable splitId: UUID,
        @PathVariable splitExerciseId: UUID,
        @RequestBody request: SplitExerciseRequest
    ): SplitExerciseEntity {
        return splitExerciseService.updateExercise(
            splitId,
            splitExerciseId,
            request
        )
    }
}

data class SplitExerciseRequest(
    val exerciseId: String,
    val exerciseName: String,
    val exerciseType: String,
    val exerciseOrder: Int,
    val targetSets: Int? = null,
    val targetRepsMin: Int? = null,
    val targetRepsMax: Int? = null,
    val targetDurationMinutes: Int? = null
)
