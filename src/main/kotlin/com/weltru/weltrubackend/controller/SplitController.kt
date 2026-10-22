
package com.weltru.weltrubackend.controller

import com.weltru.weltrubackend.dto.CreateSplitRequest
import com.weltru.weltrubackend.dto.SplitMetadataResponse
import com.weltru.weltrubackend.dto.UpdateSplitRequest
import com.weltru.weltrubackend.entity.SplitEntity
import com.weltru.weltrubackend.service.SplitService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/splits")
class SplitController(
    private val splitService: SplitService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSplit(
        @RequestBody request: CreateSplitRequest
    ): SplitEntity {
        return splitService.createSplit(request)
    }

    @GetMapping
    fun getAllSplits(
        @RequestParam userId: UUID
    ): List<SplitMetadataResponse> {
        return splitService.getAllSplits(userId)
    }
    @PutMapping("/{splitId}")
    fun updateSplit(
        @PathVariable splitId: UUID,
        @RequestBody request: UpdateSplitRequest
    ): SplitEntity {
        return splitService.updateSplit(splitId, request)
    }
}
