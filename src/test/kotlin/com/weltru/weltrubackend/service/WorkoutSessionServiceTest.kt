package com.weltru.weltrubackend.service

import com.weltru.weltrubackend.entity.WorkoutSessionEntity
import com.weltru.weltrubackend.repository.WorkoutSessionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.time.OffsetDateTime
import java.util.Optional
import java.util.UUID

class WorkoutSessionServiceTest {

    private lateinit var workoutSessionRepository: WorkoutSessionRepository
    private lateinit var workoutSessionService: WorkoutSessionService

    @BeforeEach
    fun setUp() {
        workoutSessionRepository = mock(WorkoutSessionRepository::class.java)
        workoutSessionService = WorkoutSessionService(workoutSessionRepository)
    }

    @Test
    fun `startWorkout creates new session when id not found`() {
        val userId = UUID.randomUUID()
        val splitId = UUID.randomUUID()

        `when`(workoutSessionRepository.save(any(WorkoutSessionEntity::class.java))).thenAnswer { invocation ->
            val entity = invocation.getArgument<WorkoutSessionEntity>(0)
            if (entity.id == null) {
                entity.id = UUID.randomUUID()
            }
            entity
        }

        val session = workoutSessionService.startWorkout(
            userId = userId,
            splitId = splitId,
            splitNameSnapshot = "Push Day"
        )

        assertNotNull(session.id)
        assertEquals(userId, session.userId)
        assertEquals(splitId, session.splitId)
        assertEquals("Push Day", session.splitNameSnapshot)
        assertEquals("IN_PROGRESS", session.status)
        verify(workoutSessionRepository, times(1)).save(any(WorkoutSessionEntity::class.java))
    }

    @Test
    fun `startWorkout returns existing session if id already exists`() {
        val sessionId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val existingSession = WorkoutSessionEntity(
            id = sessionId,
            userId = userId,
            status = "IN_PROGRESS"
        )

        `when`(workoutSessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession))

        val session = workoutSessionService.startWorkout(
            id = sessionId,
            userId = userId
        )

        assertEquals(sessionId, session.id)
        verify(workoutSessionRepository, never()).save(any(WorkoutSessionEntity::class.java))
    }

    @Test
    fun `completeWorkout transitions in-progress session to completed`() {
        val sessionId = UUID.randomUUID()
        val existingSession = WorkoutSessionEntity(
            id = sessionId,
            userId = UUID.randomUUID(),
            status = "IN_PROGRESS"
        )

        `when`(workoutSessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession))
        `when`(workoutSessionRepository.save(any(WorkoutSessionEntity::class.java))).thenAnswer { it.getArgument(0) }

        val completed = workoutSessionService.completeWorkout(sessionId)

        assertEquals("COMPLETED", completed.status)
        assertNotNull(completed.finishedAt)
    }

    @Test
    fun `getWorkoutSession throws exception when not found`() {
        val sessionId = UUID.randomUUID()
        `when`(workoutSessionRepository.findById(sessionId)).thenReturn(Optional.empty())

        assertThrows(IllegalArgumentException::class.java) {
            workoutSessionService.getWorkoutSession(sessionId)
        }
    }
}
