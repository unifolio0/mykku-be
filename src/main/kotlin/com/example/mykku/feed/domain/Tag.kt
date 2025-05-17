package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "is_event", nullable = false)
    var isEvent: Boolean = false,

    @Column(name = "is_contest", nullable = false)
    var isContest: Boolean = false,

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var feeds: MutableList<FeedTag> = mutableListOf(),

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var tagImages: MutableList<TagImage> = mutableListOf(),
) {
}
