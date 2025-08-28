package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
class EventImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "url")
    var url: String,

    @Column(name = "order_index")
    var orderIndex: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    val event: Event,
) : BaseEntity() {
}
