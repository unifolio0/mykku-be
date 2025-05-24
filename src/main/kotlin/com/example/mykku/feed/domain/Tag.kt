package com.example.mykku.feed.domain

import com.example.mykku.common.domain.BaseEntity
import jakarta.persistence.*

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

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_tag_id")
    val feeds: MutableList<FeedTag> = mutableListOf(),

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_image_id")
    val tagImages: MutableList<TagImage> = mutableListOf(),
) : BaseEntity() {
}
