package com.example.mykku.scrap.adapter.output.persistence.entity

import com.example.mykku.common.adapter.persistence.BaseJpaEntity
import com.example.mykku.fannote.adapter.output.persistence.entity.FanNoteJpaEntity
import com.example.mykku.member.adapter.output.persistence.entity.MemberJpaEntity
import com.example.mykku.scrap.domain.entity.SaveFanNoteEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "save_fan_note")
class SaveFanNoteJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fan_note_id")
    val fanNote: FanNoteJpaEntity
) : BaseJpaEntity() {

    fun toDomain(): SaveFanNoteEntity {
        return SaveFanNoteEntity.reconstitute(
            id = this.id!!,
            memberId = this.member.id,
            fanNoteId = this.fanNote.id!!,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    companion object {
        fun fromDomain(
            domain: SaveFanNoteEntity,
            member: MemberJpaEntity,
            fanNote: FanNoteJpaEntity
        ): SaveFanNoteJpaEntity {
            return SaveFanNoteJpaEntity(
                id = domain.id?.value,
                member = member,
                fanNote = fanNote
            )
        }
    }
}
