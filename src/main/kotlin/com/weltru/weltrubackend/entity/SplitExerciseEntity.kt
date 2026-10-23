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
@Table(name = "split_exercises")
class SplitExerciseEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "split_id", nullable = false)
    var splitId: UUID,

    @Column(name = "exercise_id", nullable = false, length = 100)
    var exerciseId: String,

    @Column(name = "exercise_name", nullable = false, length = 150)
    var exerciseName: String,

    @Column(name = "exercise_type", nullable = false, length = 20)
    var exerciseType: String = "STRENGTH",

    @Column(name = "exercise_order", nullable = false)
    var exerciseOrder: Int,

    @Column(name = "target_sets")
    var targetSets: Int? = 3,

    @Column(name = "target_reps_min")
    var targetRepsMin: Int? = 8,

    @Column(name = "target_reps_max")
    var targetRepsMax: Int? = 12,

    @Column(name = "target_duration_minutes")
    var targetDurationMinutes: Int? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null

) {

    @PrePersist
    fun onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now()
        }
    }
}
