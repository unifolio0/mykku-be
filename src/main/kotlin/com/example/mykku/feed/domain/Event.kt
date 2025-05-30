package com.example.mykku.feed.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("EVENT")
class Event(
    title: String,
    expiredAt: LocalDateTime,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val eventTags: MutableList<EventTag> = mutableListOf(),
) : BasicEvent(title = title, expiredAt = expiredAt) {
    override fun getType(): String = "EVENT"
}
