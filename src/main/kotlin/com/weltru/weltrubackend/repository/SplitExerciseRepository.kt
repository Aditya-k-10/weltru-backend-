package com.weltru.weltrubackend.repository



import com.weltru.weltrubackend.entity.SplitExerciseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SplitExerciseRepository : JpaRepository<SplitExerciseEntity, UUID> {

    fun findAllBySplitIdOrderByExerciseOrder(
        splitId: UUID
    ): List<SplitExerciseEntity>

    fun existsBySplitIdAndExerciseOrder(
        splitId: UUID,
        exerciseOrder: Int
    ): Boolean
    fun deleteByIdAndSplitId(
        id: UUID,
        splitId: UUID
    )
}
