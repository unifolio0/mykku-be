package com.example.mykku.feed.domain

import jakarta.persistence.*

@Entity
class Tag(
    @Column(name = "title")
    val title: String,

    @Column(name = "is_event")
    val isEvent: Boolean = false,

    @Column(name = "is_contest")
    val isContest: Boolean = false,

    @OneToMany(mappedBy = "tag")
    val feeds: List<FeedTag> = mutableListOf(),

    @OneToMany(mappedBy = "tag")
    val tagImages: List<TagImage> = mutableListOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
