package com.weltru.weltrubackend.entity



import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "splits")
class SplitEntity(

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    @Column(name = "description")
    var description: String? = null,



    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
var createdAt: OffsetDateTime? = null,

@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
var updatedAt: OffsetDateTime? = null
)
