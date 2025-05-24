package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "is_event", nullable = false)
    var isEvent: Boolean = false,

    @Column(name = "is_contest", nullable = false)
    var isContest: Boolean = false,

    @Column(name = "expired_at", nullable = false)
    var expiredAt: LocalDateTime,

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var feeds: MutableList<FeedTag> = mutableListOf(),

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var tagImages: MutableList<TagImage> = mutableListOf(),
) : BaseEntity() {
}
