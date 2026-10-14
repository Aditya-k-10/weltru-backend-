package com.weltru.weltrubackend.service



import com.weltru.weltrubackend.dto.CreateSplitRequest
import com.weltru.weltrubackend.entity.SplitEntity
import com.weltru.weltrubackend.repository.SplitRepository
import org.springframework.stereotype.Service

@Service
class SplitService(
    private val splitRepository: SplitRepository
) {

    fun createSplit(request: CreateSplitRequest): SplitEntity {
        val split = SplitEntity(
            userId = request.userId,
            name = request.name,
            description = request.description
        )

        return splitRepository.save(split)
    }
}
