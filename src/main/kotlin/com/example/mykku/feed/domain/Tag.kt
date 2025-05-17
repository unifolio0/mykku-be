package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "is_event")
    var isEvent: Boolean = false,

    @Column(name = "is_contest")
    var isContest: Boolean = false,

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val feeds: List<FeedTag> = mutableListOf(),

    @OneToMany(mappedBy = "tag", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val tagImages: List<TagImage> = mutableListOf(),
) {
}
