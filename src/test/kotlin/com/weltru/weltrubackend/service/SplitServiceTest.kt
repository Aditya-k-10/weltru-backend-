package com.weltru.weltrubackend.service

import com.weltru.weltrubackend.dto.CreateSplitRequest
import com.weltru.weltrubackend.entity.SplitEntity
import com.weltru.weltrubackend.repository.SessionSetRepository
import com.weltru.weltrubackend.repository.SplitExerciseRepository
import com.weltru.weltrubackend.repository.SplitRepository
import com.weltru.weltrubackend.repository.WorkoutSessionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.UUID

class SplitServiceTest {

    private lateinit var splitRepository: SplitRepository
    private lateinit var splitExerciseRepository: SplitExerciseRepository
    private lateinit var workoutSessionRepository: WorkoutSessionRepository
    private lateinit var sessionSetRepository: SessionSetRepository
    private lateinit var splitService: SplitService

    @BeforeEach
    fun setUp() {
        splitRepository = mock(SplitRepository::class.java)
        splitExerciseRepository = mock(SplitExerciseRepository::class.java)
        workoutSessionRepository = mock(WorkoutSessionRepository::class.java)
        sessionSetRepository = mock(SessionSetRepository::class.java)
        splitService = SplitService(
            splitRepository,
            splitExerciseRepository,
            workoutSessionRepository,
            sessionSetRepository
        )
    }

    @Test
    fun `createSplit populates createdAt and updatedAt and saves split successfully`() {
        val userId = UUID.randomUUID()
        val request = CreateSplitRequest(
            userId = userId,
            name = "Push Day"
        )

        `when`(splitRepository.findAllByUserId(userId)).thenReturn(emptyList())
        `when`(splitRepository.save(any(SplitEntity::class.java))).thenAnswer { invocation ->
            val entity = invocation.getArgument<SplitEntity>(0)
            entity.id = UUID.randomUUID()
            entity
        }

        val result = splitService.createSplit(request)

        assertNotNull(result.id)
        assertEquals("Push Day", result.name)
        assertEquals(userId, result.userId)
        assertNotNull(result.createdAt, "createdAt must not be null on creation")
        assertNotNull(result.updatedAt, "updatedAt must not be null on creation")
        verify(splitRepository, times(1)).save(any(SplitEntity::class.java))
    }
}
