package com.weltru.weltrubackend.repository

import com.weltru.weltrubackend.entity.SplitEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SplitRepository : JpaRepository<SplitEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<SplitEntity>
}
