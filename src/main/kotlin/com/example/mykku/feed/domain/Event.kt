package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class Event(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title")
    var title: String,

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val eventTags: MutableList<EventTag> = mutableListOf(),
) : BaseEntity() {
}
