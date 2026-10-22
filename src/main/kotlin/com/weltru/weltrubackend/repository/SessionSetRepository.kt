package com.weltru.weltrubackend.repository

import java.util.Optional

import com.weltru.weltrubackend.entity.SessionSetEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SessionSetRepository : JpaRepository<SessionSetEntity, UUID> {

    fun findAllBySessionIdOrderBySetNumberAsc(
        sessionId: UUID
    ): List<SessionSetEntity>
    fun findByIdAndSessionId(
        id: UUID,
        sessionId: UUID
    ): Optional<SessionSetEntity>
    
    fun deleteAllBySessionId(sessionId: UUID)
}

