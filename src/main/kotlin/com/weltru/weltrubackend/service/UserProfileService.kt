package com.weltru.weltrubackend.service

import com.weltru.weltrubackend.dto.UserProfileDto
import com.weltru.weltrubackend.entity.UserProfileEntity
import com.weltru.weltrubackend.repository.UserProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserProfileService(
    private val userProfileRepository: UserProfileRepository
) {

    @Transactional(readOnly = true)
    fun getProfile(userId: UUID): UserProfileDto {
        val entity = userProfileRepository.findByUserId(userId).orElse(null)
            ?: return UserProfileDto(userId = userId) // Return empty DTO if not found

        return UserProfileDto(
            userId = entity.userId,
            name = entity.name,
            email = entity.email,
            bodyWeightKg = entity.bodyWeightKg,
            heightCm = entity.heightCm,
            ageYears = entity.ageYears,
            sex = entity.sex,
            activeCalorieGoal = entity.activeCalorieGoal
        )
    }

    @Transactional
    fun upsertProfile(userId: UUID, request: UserProfileDto): UserProfileDto {
        val existing = userProfileRepository.findByUserId(userId)
        val entity = if (existing.isPresent) {
            val profile = existing.get()
            profile.name = request.name
            profile.email = request.email
            profile.bodyWeightKg = request.bodyWeightKg
            profile.heightCm = request.heightCm
            profile.ageYears = request.ageYears
            profile.sex = request.sex
            profile.activeCalorieGoal = request.activeCalorieGoal
            profile
        } else {
            UserProfileEntity(
                userId = userId,
                name = request.name,
                email = request.email,
                bodyWeightKg = request.bodyWeightKg,
                heightCm = request.heightCm,
                ageYears = request.ageYears,
                sex = request.sex,
                activeCalorieGoal = request.activeCalorieGoal
            )
        }

        val saved = userProfileRepository.save(entity)
        return UserProfileDto(
            userId = saved.userId,
            name = saved.name,
            email = saved.email,
            bodyWeightKg = saved.bodyWeightKg,
            heightCm = saved.heightCm,
            ageYears = saved.ageYears,
            sex = saved.sex,
            activeCalorieGoal = saved.activeCalorieGoal
        )
    }
}
