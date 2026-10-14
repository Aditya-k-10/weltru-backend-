package com.weltru.weltrubackend.dto



import java.util.UUID

data class CreateSplitRequest(
    val userId: UUID,
    val name: String,
    val description: String? = null
)
