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
@Table(name = "user_profiles")
class UserProfileEntity(

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "user_id", nullable = false, unique = true)
    var userId: UUID,

    @Column(name = "name", length = 150)
    var name: String? = null,

    @Column(name = "email", length = 255)
    var email: String? = null,

    @Column(name = "body_weight_kg", precision = 7, scale = 2)
    var bodyWeightKg: BigDecimal? = null,

    @Column(name = "height_cm", precision = 7, scale = 2)
    var heightCm: BigDecimal? = null,

    @Column(name = "age_years")
    var ageYears: Int? = null,

    @Column(name = "sex", length = 20)
    var sex: String? = null,

    @Column(name = "active_calorie_goal")
    var activeCalorieGoal: Int? = null,

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    var updatedAt: OffsetDateTime? = null
)
