package com.weltru.weltrubackend.controller

import com.weltru.weltrubackend.dto.UserProfileDto
import com.weltru.weltrubackend.service.UserProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

import org.springframework.web.bind.annotation.CrossOrigin

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/api/profiles")
class UserProfileController(
    private val userProfileService: UserProfileService
) {

    @GetMapping
    fun getProfile(
        @RequestParam userId: UUID
    ): ResponseEntity<UserProfileDto> {
        val response = userProfileService.getProfile(userId)
        return ResponseEntity.ok(response)
    }

    @PutMapping
    fun upsertProfile(
        @RequestBody request: UserProfileDto
    ): ResponseEntity<UserProfileDto> {
        val response = userProfileService.upsertProfile(request.userId, request)
        return ResponseEntity.ok(response)
    }
}
