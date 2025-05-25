package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*
import java.util.*

@Entity
class Tag(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "is_event", nullable = false)
    var isEvent: Boolean = false,

    @Column(name = "is_contest", nullable = false)
    var isContest: Boolean = false,

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feeds: MutableList<FeedTag> = mutableListOf(),

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val tagImages: MutableList<TagImage> = mutableListOf(),
) : BaseEntity() {
}
