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
        val split = SplitEntity(
            userId = request.userId,
            name = request.name,
            description = request.description
        )

        return splitRepository.save(split)
    }



    fun getAllSplits(): List<SplitMetadataResponse> {
        return splitRepository.findAll().map { split ->
            val splitId = requireNotNull(split.id)

            val exercises =
                splitExerciseRepository
                    .findAllBySplitIdOrderByExerciseOrder(splitId)

            SplitMetadataResponse(
                splitId = splitId,
                splitName = split.name,
                metadata = SplitMetadata(
                    description = split.description,
                    exerciseCount = exercises.size,
                    totalSets = exercises.sumOf { it.targetSets ?: 0 }
                )
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
