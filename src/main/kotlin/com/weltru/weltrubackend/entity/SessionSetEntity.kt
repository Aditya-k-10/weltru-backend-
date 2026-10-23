package com.weltru.weltrubackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.Table
import jakarta.persistence.Transient
import org.springframework.data.domain.Persistable
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "session_sets")
class SessionSetEntity(

    @Id
    @Column(name = "id", nullable = false)
    private var id: UUID? = null,

    @Column(name = "session_id", nullable = false)
    var sessionId: UUID,

    @Column(name = "split_exercise_id")
    var splitExerciseId: UUID? = null,

    @Column(name = "exercise_id", nullable = false, length = 100)
    var exerciseId: String,

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
) : Persistable<UUID> {

    @Transient
    private var isNewEntity: Boolean = true

    override fun getId(): UUID? = id

    fun setId(id: UUID?) {
        this.id = id
    }

    override fun isNew(): Boolean = isNewEntity

    @PostPersist
    @PostLoad
    fun markNotNew() {
        this.isNewEntity = false
    }
}
