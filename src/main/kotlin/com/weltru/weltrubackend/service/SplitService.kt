package com.weltru.weltrubackend.service

import com.weltru.weltrubackend.dto.CreateSplitRequest
import com.weltru.weltrubackend.dto.SplitMetadata
import com.weltru.weltrubackend.dto.SplitMetadataResponse
import com.weltru.weltrubackend.dto.UpdateSplitRequest
import com.weltru.weltrubackend.entity.SplitEntity
import com.weltru.weltrubackend.repository.SplitExerciseRepository
import com.weltru.weltrubackend.repository.SplitRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SplitService(
    private val splitRepository: SplitRepository,
    private val splitExerciseRepository: SplitExerciseRepository
) {

    fun createSplit(request: CreateSplitRequest): SplitEntity {
        val trimmedName = request.name.trim()
        val existingSplits = splitRepository.findAllByUserId(request.userId)
        if (existingSplits.any { it.name.trim().equals(trimmedName, ignoreCase = true) }) {
            throw IllegalArgumentException("A split named '$trimmedName' already exists")
        }

        val split = SplitEntity(
            userId = request.userId,
            name = trimmedName,
            description = request.description
        )

        return splitRepository.save(split)
    }

    fun getAllSplits(userId: UUID): List<SplitMetadataResponse> {
        val splits = splitRepository.findAllByUserId(userId)
        if (splits.isEmpty()) return emptyList()

        val splitIds = splits.mapNotNull { it.id }
        val allExercises = splitExerciseRepository.findAllBySplitIdIn(splitIds)
        val exercisesBySplitId = allExercises.groupBy { it.splitId }

        return splits.map { split ->
            val splitId = requireNotNull(split.id)
            val exercises = exercisesBySplitId[splitId].orEmpty().sortedBy { it.exerciseOrder }

            SplitMetadataResponse(
                splitId = splitId,
                splitName = split.name,
                metadata = SplitMetadata(
                    description = split.description,
                    exerciseCount = exercises.size,
                    totalSets = exercises.sumOf { it.targetSets ?: 0 }
                ),
                exercises = exercises
            )
        }
    }

    fun updateSplit(
        splitId: UUID,
        request: UpdateSplitRequest
    ): SplitEntity {
        val split = splitRepository.findById(splitId)
            .orElseThrow {
                IllegalArgumentException("Split not found: $splitId")
            }

        split.name = request.name
        return splitRepository.save(split)
    }
}
