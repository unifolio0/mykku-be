package com.example.mykku.fannote.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.fannote.domain.entity.FanNotePage
import com.example.mykku.fannote.domain.vo.FanNotePageId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "fan_note_page")
class FanNotePageJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var pageNumber: Int,

    @Column(nullable = false, length = 500)
    var imageUrl: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fan_note_id", nullable = false)
    var fanNote: FanNoteJpaEntity
) : BaseJpaEntity() {

    fun toDomain(): FanNotePage {
        return FanNotePage.reconstitute(
            id = FanNotePageId.of(id!!),
            fanNoteId = fanNote.id!!,
            pageNumber = pageNumber,
            imageUrl = imageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun updateFromDomain(page: FanNotePage) {
        this.pageNumber = page.pageNumber
        this.imageUrl = page.imageUrl
    }

    companion object {
        fun fromDomain(page: FanNotePage, fanNoteJpaEntity: FanNoteJpaEntity): FanNotePageJpaEntity {
            return FanNotePageJpaEntity(
                id = if (page.id.value == 0L) null else page.id.value,
                pageNumber = page.pageNumber,
                imageUrl = page.imageUrl,
                fanNote = fanNoteJpaEntity
            )
        }
    }
}
