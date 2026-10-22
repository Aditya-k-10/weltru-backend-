
package com.weltru.weltrubackend.service

import com.weltru.weltrubackend.entity.SplitExerciseEntity
import com.weltru.weltrubackend.repository.SplitExerciseRepository
import com.weltru.weltrubackend.repository.SplitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SplitExerciseService(
    private val splitExerciseRepository: SplitExerciseRepository,
    private val splitRepository: SplitRepository
) {

    @Transactional
    fun addExercise(
        splitId: UUID,
        exercise: SplitExerciseEntity
    ): SplitExerciseEntity {

        require(splitRepository.existsById(splitId)) {
            "Split not found: $splitId"
        }

        require(
            exercise.exerciseType == "STRENGTH" ||
                    exercise.exerciseType == "CARDIO"
        ) {
            "Exercise type must be STRENGTH or CARDIO"
        }

        require(exercise.exerciseOrder >= 0) {
            "Exercise order cannot be negative"
        }

        if (exercise.exerciseType == "STRENGTH") {

            require((exercise.targetSets ?: 0) > 0) {
                "Target sets must be greater than zero"
            }

            require((exercise.targetRepsMin ?: 0) > 0) {
                "Minimum target reps must be greater than zero"
            }

            require(
                (exercise.targetRepsMax ?: 0) >=
                        (exercise.targetRepsMin ?: 0)
            ) {
                "Maximum target reps must be greater than or equal to minimum target reps"
            }

            require(exercise.targetDurationMinutes == null) {
                "Strength exercises cannot have a target duration"
            }

        } else {

            require((exercise.targetDurationMinutes ?: 0) > 0) {
                "Cardio target duration must be greater than zero"
            }

            require(
                exercise.targetSets == null &&
                        exercise.targetRepsMin == null &&
                        exercise.targetRepsMax == null
            ) {
                "Cardio exercises cannot have target sets or reps"
            }
        }

        if (splitExerciseRepository.existsBySplitIdAndExerciseOrder(splitId, exercise.exerciseOrder)) {
            val maxOrder = splitExerciseRepository.findAllBySplitIdOrderByExerciseOrder(splitId)
                .maxOfOrNull { it.exerciseOrder } ?: 0
            exercise.exerciseOrder = maxOrder + 1
        }

        exercise.splitId = splitId

        return splitExerciseRepository.save(exercise)
    }

    @Transactional(readOnly = true)
    fun getExercisesBySplitId(
        splitId: UUID
    )
    : List<SplitExerciseEntity> {

        require(splitRepository.existsById(splitId)) {
            "Split not found: $splitId"
        }

        return splitExerciseRepository
            .findAllBySplitIdOrderByExerciseOrder(splitId)
    }
    @Transactional
    fun deleteExercise(
        splitId: UUID,
        splitExerciseId: UUID
    ) {
        require(splitRepository.existsById(splitId)) {
            "Split not found: $splitId"
        }

        val exercise = splitExerciseRepository.findById(splitExerciseId)
            .orElseThrow {
                IllegalArgumentException(
                    "Exercise not found: $splitExerciseId"
                )
            }

        require(exercise.splitId == splitId) {
            "Exercise does not belong to this split"
        }

        splitExerciseRepository.delete(exercise)
    }
}
