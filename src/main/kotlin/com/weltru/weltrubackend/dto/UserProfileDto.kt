package com.weltru.weltrubackend.dto

import java.math.BigDecimal
import java.util.UUID

data class UserProfileDto(
    val userId: UUID,
    val name: String? = null,
    val email: String? = null,
    val bodyWeightKg: BigDecimal? = null,
    val heightCm: BigDecimal? = null,
    val ageYears: Int? = null,
    val sex: String? = null,
    val activeCalorieGoal: Int? = null
)
