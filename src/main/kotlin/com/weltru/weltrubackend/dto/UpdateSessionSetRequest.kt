package com.weltru.weltrubackend.dto


import java.math.BigDecimal

data class UpdateSessionSetRequest(
    val actualReps: Int? = null,
    val weightKg: BigDecimal? = null,
    val durationMinutes: Int? = null
)