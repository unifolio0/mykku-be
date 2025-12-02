package com.example.mykku.event.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime,

    @Column(name = "scrap_count")
    var scrapCount: Int = 0,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: EventStatusType = EventStatusType.ACTIVE,
) : BaseEntity() {
    companion object {
        const val IMAGE_MAX_COUNT = 10
    }
}
