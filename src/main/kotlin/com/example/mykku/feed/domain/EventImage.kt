package com.example.mykku.feed.domain

import jakarta.persistence.*
import java.util.*

@Entity
class EventImage(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "url")
    var url: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event,
) {
}
