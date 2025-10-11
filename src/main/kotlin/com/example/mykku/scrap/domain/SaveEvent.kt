package com.example.mykku.scrap.domain

import com.example.mykku.common.domain.BaseEntity
import com.example.mykku.feed.domain.Event
import com.example.mykku.member.domain.Member
import jakarta.persistence.*

@Entity
class SaveEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event
) : BaseEntity() {
}
