package com.example.mykku.feed.domain

import jakarta.persistence.*
import java.util.*

@Entity
class BasicEventImage(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "url")
    var url: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_event_id")
    val basicEvent: BasicEvent,
) {
}
