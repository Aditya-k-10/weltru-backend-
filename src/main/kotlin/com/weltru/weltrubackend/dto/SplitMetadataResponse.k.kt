package com.weltru.weltrubackend.dto

import com.weltru.weltrubackend.entity.SplitExerciseEntity
import java.util.UUID

data class SplitMetadataResponse(
    val splitId: UUID,
    val splitName: String,
    val metadata: SplitMetadata,
    val exercises: List<SplitExerciseEntity> = emptyList()
)

data class SplitMetadata(
    val exerciseCount: Int,
    val totalSets: Int,
    val createdAt: String? = null
)
