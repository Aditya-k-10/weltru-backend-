package com.weltru.weltrubackend.controller



import com.weltru.weltrubackend.dto.CreateSplitRequest
import com.weltru.weltrubackend.entity.SplitEntity
import com.weltru.weltrubackend.service.SplitService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
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
}
