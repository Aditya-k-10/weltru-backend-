package com.weltru.weltrubackend.entity



import jakarta.persistence.Column
import jakarta.persistence.Entity

import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "session_sets")
class SessionSetEntity(

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "session_id", nullable = false)
    var sessionId: UUID,

    @Column(name = "split_exercise_id")
    var splitExerciseId: UUID? = null,

    @Column(name = "exercise_id", nullable = false)
    var exerciseId: UUID,

    @Column(name = "exercise_name_snapshot", nullable = false, length = 150)
    var exerciseNameSnapshot: String,

    @Column(name = "set_number", nullable = false)
    var setNumber: Int,

    @Column(name = "target_reps")
    var targetReps: Int? = null,

    @Column(name = "actual_reps")
    var actualReps: Int? = null,

    @Column(name = "weight_kg", precision = 7, scale = 2)
    var weightKg: BigDecimal? = null,

    @Column(name = "duration_minutes")
    var durationMinutes: Int? = null,

    @Column(name = "is_completed", nullable = false)
    var isCompleted: Boolean = false,

    @Column(name = "completed_at")
    var completedAt: OffsetDateTime? = null
)
