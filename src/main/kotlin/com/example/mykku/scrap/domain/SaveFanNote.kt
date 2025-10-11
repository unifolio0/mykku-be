package com.example.mykku.scrap.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.fannote.domain.FanNote
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
class SaveFanNote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fan_note_id")
    val fanNote: FanNote
) : BaseEntity() {
}
