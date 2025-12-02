package com.example.mykku.contest.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Contest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "started_at")
    var startedAt: LocalDateTime,

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime,

    @Column(name = "scrap_count")
    var scrapCount: Int = 0,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: ContestStatusType = ContestStatusType.ACTIVE,
) : BaseEntity() {
    companion object {
        const val IMAGE_MAX_COUNT = 10
        const val TAG_MAX_COUNT = 7
    }
}
