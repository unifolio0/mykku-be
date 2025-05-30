package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
@DiscriminatorValue("EVENT_TAG")
class EventTag(
    title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event,
) : Tag(
    title = title
) {
    override fun getType(): String = "EVENT_TAG"
}
