package com.weltru.weltrubackend.repository

import com.weltru.weltrubackend.controller.WorkoutSessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkoutSessionRepository :
    JpaRepository<WorkoutSessionEntity, UUID>