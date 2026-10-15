
package com.weltru.weltrubackend.dto

import java.util.UUID

data class SplitMetadataResponse(
    val splitId: UUID,
    val splitName: String,
    val metadata: SplitMetadata
)

data class SplitMetadata(
    val description: String?,
    val exerciseCount: Int,
    val totalSets: Int
)
