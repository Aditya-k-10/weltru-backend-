package com.weltru.weltrubackend.repository

import com.weltru.weltrubackend.entity.SessionSetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

interface SessionSetRepository : JpaRepository<SessionSetEntity, UUID> {

    fun findAllBySessionIdOrderBySetNumberAsc(
        sessionId: UUID
    ): List<SessionSetEntity>

    fun findByIdAndSessionId(
        id: UUID,
        sessionId: UUID
    ): Optional<SessionSetEntity>

    @Modifying
    @Transactional
    @Query("DELETE FROM SessionSetEntity s WHERE s.sessionId = :sessionId")
    fun deleteAllBySessionId(sessionId: UUID)
}
