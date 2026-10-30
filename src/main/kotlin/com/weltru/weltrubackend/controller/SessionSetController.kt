package com.weltru.weltrubackend.controller




import com.weltru.weltrubackend.dto.CreateSessionSetRequest
import com.weltru.weltrubackend.dto.UpdateSessionSetRequest
import com.weltru.weltrubackend.entity.SessionSetEntity
import com.weltru.weltrubackend.service.SessionSetService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

import java.util.UUID

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/api/workout-sessions/{sessionId}/sets")
class SessionSetController(
    private val sessionSetService: SessionSetService
) {

    @GetMapping
    fun getSessionSets(
        @PathVariable sessionId: UUID
    ): List<SessionSetEntity> {
        return sessionSetService.getSessionSets(sessionId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSessionSet(
        @PathVariable sessionId: UUID,
        @RequestBody request: CreateSessionSetRequest
    ): SessionSetEntity {
        return sessionSetService.createSessionSet(sessionId, request)
    }
    @GetMapping("/{setId}")
    fun getSessionSet(
        @PathVariable sessionId: UUID,
        @PathVariable setId: UUID
    ): SessionSetEntity {
        return sessionSetService.getSessionSet(
            sessionId = sessionId,
            setId = setId
        )
    }
    @PatchMapping("/{setId}")
    fun updateSessionSet(
        @PathVariable sessionId: UUID,
        @PathVariable setId: UUID,
        @RequestBody request: UpdateSessionSetRequest
    ): SessionSetEntity {
        return sessionSetService.updateSessionSet(
            sessionId = sessionId,
            setId = setId,
            request = request
        )
    }
    @PatchMapping("/{setId}/complete")
    fun completeSessionSet(
        @PathVariable sessionId: UUID,
        @PathVariable setId: UUID
    ): SessionSetEntity {
        return sessionSetService.completeSessionSet(
            sessionId = sessionId,
            setId = setId
        )
    }
    @DeleteMapping("/{setId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSessionSet(
        @PathVariable sessionId: UUID,
        @PathVariable setId: UUID
    ) {
        sessionSetService.deleteSessionSet(
            sessionId = sessionId,
            setId = setId
        )
    }
}
