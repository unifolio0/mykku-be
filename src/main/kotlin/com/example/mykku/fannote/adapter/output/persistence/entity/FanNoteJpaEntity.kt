package com.example.mykku.fannote.adapter.output.persistence.entity

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.fannote.domain.entity.FanNote
import com.example.mykku.fannote.domain.vo.FanNoteId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "fan_note")
class FanNoteJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    var title: String,

    @Column(nullable = true, length = 200)
    var subtitle: String? = null,

    @Column(columnDefinition = "TEXT")
    var content: String? = null,

    @Column(nullable = false)
    var productionDate: LocalDate,

    @Column(nullable = true, length = 500)
    var coverImageUrl: String? = null
) : BaseEntity() {

    fun toDomain(): FanNote {
        return FanNote.reconstitute(
            id = FanNoteId.of(id!!),
            title = title,
            subtitle = subtitle,
            content = content,
            productionDate = productionDate,
            coverImageUrl = coverImageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun updateFromDomain(fanNote: FanNote) {
        this.title = fanNote.title
        this.subtitle = fanNote.subtitle
        this.content = fanNote.content
        this.productionDate = fanNote.productionDate
        this.coverImageUrl = fanNote.coverImageUrl
    }

    companion object {
        fun fromDomain(fanNote: FanNote): FanNoteJpaEntity {
            return FanNoteJpaEntity(
                id = if (fanNote.id.value == 0L) null else fanNote.id.value,
                title = fanNote.title,
                subtitle = fanNote.subtitle,
                content = fanNote.content,
                productionDate = fanNote.productionDate,
                coverImageUrl = fanNote.coverImageUrl
            )
        }
    }
}
