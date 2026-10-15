package com.weltru.weltrubackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "workout_sessions")
class WorkoutSessionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "split_id")
    var splitId: UUID? = null,

    @Column(name = "split_name_snapshot", length = 100)
    var splitNameSnapshot: String? = null,

    @Column(name = "started_at", nullable = false, updatable = false)
    var startedAt: OffsetDateTime? = null,

    @Column(name = "finished_at")
    var finishedAt: OffsetDateTime? = null,

    @Column(name = "status", nullable = false, length = 20)
    var status: String = "IN_PROGRESS",

    @Column(name = "notes")
    var notes: String? = null
) {
    @PrePersist
    fun onCreate() {
        if (startedAt == null) {
            startedAt = OffsetDateTime.now()
        }
    }
}
