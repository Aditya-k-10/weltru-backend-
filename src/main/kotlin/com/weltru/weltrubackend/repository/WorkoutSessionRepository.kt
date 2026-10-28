package com.weltru.weltrubackend.repository

import com.weltru.weltrubackend.entity.WorkoutSessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkoutSessionRepository :
    JpaRepository<WorkoutSessionEntity, UUID> {
    fun findAllByUserIdOrderByStartedAtDesc(userId: UUID): List<WorkoutSessionEntity>
    fun findAllBySplitId(splitId: UUID): List<WorkoutSessionEntity>
}
