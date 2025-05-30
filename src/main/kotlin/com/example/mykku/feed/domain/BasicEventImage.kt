package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class BasicEventImage(
    @Id
    val id: Long? = null,

    @Column(name = "url")
    var url: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_event_id")
    val basicEvent: BasicEvent,
) {
}
