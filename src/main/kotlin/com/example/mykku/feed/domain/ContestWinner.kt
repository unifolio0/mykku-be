package com.example.mykku.feed.domain

import jakarta.persistence.*

class ContestWinner(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "rank", nullable = false)
    var rank: Int,

    @Column(name = "description")
    var description: String,

    @Column(name = "acceptance_speech")
    var acceptanceSpeech: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    val tag: Tag,

    @OneToMany(mappedBy = "contestWinner", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feeds: MutableList<Feed> = mutableListOf(),
) {
}
